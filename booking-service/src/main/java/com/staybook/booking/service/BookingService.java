package com.staybook.booking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.staybook.booking.repository.BookingRepository;

import jakarta.transaction.Transactional;

import com.staybook.booking.dto.response.BookingDetailResponseDto;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.dto.response.HotelSummaryDto;
import com.staybook.booking.dto.response.ReviewSummaryDto;
import com.staybook.booking.entity.Booking;
import com.staybook.booking.entity.RoomAvailability;
import com.staybook.booking.enums.BookingStatus;
import com.staybook.booking.exception.BookingNotFoundException;
import com.staybook.booking.exception.InvalidDateRangeException;
import com.staybook.booking.exception.RoomNotAvailableException;
import com.staybook.booking.mapper.BookingMapper;
import com.staybook.booking.client.HotelsClient;
import com.staybook.booking.client.ReviewsClient;
import com.staybook.booking.dto.request.BookingRequestDto;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private static final String BOOKING_NOT_FOUND = "Reserva no encontrada con id: ";
    private static final String ROOM_NOT_AVAILABLE = "Habitación no disponible";
    private final ReviewsClient reviewsClient;
    private final HotelsClient hotelsClient;
    private final RoomAvailabilityService roomAvailabilityService;

    public BookingService(BookingRepository repository, BookingMapper mapper, ReviewsClient reviewsClient,
            HotelsClient hotelsClient, RoomAvailabilityService roomAvailabilityService) {
        this.repository = repository;
        this.mapper = mapper;
        this.reviewsClient = reviewsClient;
        this.hotelsClient = hotelsClient;
        this.roomAvailabilityService = roomAvailabilityService;
    }

    /**
     * Crea una reserva
     * 
     * @param BookingRequestDto request
     * @return BookingResponseDto response
     */
    @Transactional 
    public BookingResponseDto create(BookingRequestDto request) {
        if (!request.checkInDate().isBefore(request.checkOutDate())) {
            throw new InvalidDateRangeException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        List<RoomAvailability> availabilities = roomAvailabilityService.getAvailableRoomsForUpdate(request.hotelId(), request.checkInDate(), request.checkOutDate(), request.roomsRequested(), request.roomTypeId());

        int daysAvailabilities = availabilities.size();

        long daysReserved = ChronoUnit.DAYS.between(request.checkInDate(), request.checkOutDate());

        if(daysAvailabilities != daysReserved) {
            throw new RoomNotAvailableException(ROOM_NOT_AVAILABLE);
        }

        BigDecimal priceTotal = BigDecimal.ZERO;

        for (RoomAvailability availability : availabilities) {
            if (request.roomsRequested() > availability.getAvailableQuantity()) {
                throw new RoomNotAvailableException(ROOM_NOT_AVAILABLE);
            }

            availability.setAvailableQuantity(availability.getAvailableQuantity() - request.roomsRequested());

            priceTotal = priceTotal.add(availability.getPrice().multiply(BigDecimal.valueOf(request.roomsRequested())));
        }

        // Persistir disponibilidades actualizadas antes de guardar la reserva
        roomAvailabilityService.saveAllUpdated(availabilities);

        // Aplicar escala/rounding al total final
        priceTotal = priceTotal.setScale(2, RoundingMode.HALF_UP);

        Booking booking = mapper.toEntity(request);

        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(priceTotal);
        booking.setBookingReference(this.generateBookingReference());

        Booking saved = repository.save(booking);

        return mapper.toResponseDto(saved);
    }

    /**
     * Genera un número único para cada reserva
     * 
     * @return string
     */
    private String generateBookingReference() {
        String shortCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "SB-" + shortCode;
    }

    /**
     * Busca una reserva por id, si no la encuentra lanza una excepción
     * 
     * @param bookingId
     * @return
     */
    public BookingResponseDto findById(Long bookingId) {
        if (bookingId == null || bookingId < 1) {
            throw new BookingNotFoundException(BOOKING_NOT_FOUND + bookingId);
        }

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(BOOKING_NOT_FOUND + bookingId));

        return mapper.toResponseDto(booking);
    }

    /**
     * Devuelve todas las reservas del usuario
     * 
     * @param pageable
     * @return
     */
    public Page<BookingResponseDto> findAll(Pageable pageable, Long userId) {
        return repository.findAllByUserId(userId, pageable)
                .map(mapper::toResponseDto);
    }

    public BookingDetailResponseDto getBookingDetails(Long id) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(BOOKING_NOT_FOUND + id));

        HotelSummaryDto hotelSummaryDto = fetchHotelSafely(booking.getHotelId());

        List<ReviewSummaryDto> reviewSummaryDto = fetchReviewsSafely(booking.getHotelId());

        return new BookingDetailResponseDto(
                mapper.toResponseDto(booking),
                hotelSummaryDto,
                reviewSummaryDto);
    }

    private HotelSummaryDto fetchHotelSafely(Long hotelId) {
        try {
            return hotelsClient.getHotelById(hotelId);
        } catch (Exception e) {
            log.warn("No se pudo obtener el hotel {} desde hotels-service: {}", hotelId, e.getMessage());
            return null;
        }
    }

    private List<ReviewSummaryDto> fetchReviewsSafely(Long hotelId) {
        try {
            return reviewsClient.getReviewsByHotelId(hotelId);
        } catch (Exception e) {
            log.warn("No se pudo obtener la lista de reseñas del hotel {}: {}", hotelId, e.getMessage());
            return List.of();
        }
    }
}
