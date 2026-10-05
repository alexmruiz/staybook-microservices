package com.staybook.booking.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.staybook.booking.repository.BookingRepository;

import com.staybook.booking.dto.response.BookingDetailResponseDto;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.dto.response.HotelSummaryDto;
import com.staybook.booking.dto.response.ReviewSummaryDto;
import com.staybook.booking.entity.Booking;
import com.staybook.booking.entity.RoomAvailability;
import com.staybook.booking.enums.BookingStatus;
import com.staybook.booking.exception.BookingAccessDeniedException;
import com.staybook.booking.exception.BookingNotFoundException;
import com.staybook.booking.exception.InvalidBookingStateException;
import com.staybook.booking.exception.InvalidDateRangeException;
import com.staybook.booking.exception.RoomNotAvailableException;
import com.staybook.booking.mapper.BookingMapper;
import com.staybook.booking.dto.request.BookingRequestDto;

@Service
public class BookingService {

    private static final String BOOKING_NOT_FOUND = "Reserva no encontrada con id: ";
    private static final String ROOM_NOT_AVAILABLE = "Habitación no disponible";
    private static final String ACCESS_DENIED = "No tienes acceso a esta reserva";

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final RoomAvailabilityService roomAvailabilityService;
    private final ExternalHotelService externalHotelService;

    public BookingService(BookingRepository repository, BookingMapper mapper,
            RoomAvailabilityService roomAvailabilityService, ExternalHotelService externalHotelService) {
        this.repository = repository;
        this.mapper = mapper;
        this.roomAvailabilityService = roomAvailabilityService;
        this.externalHotelService = externalHotelService;
    }

    /**
     * Crea una reserva
     * 
     * @param BookingRequestDto request
     * @return BookingResponseDto response
     */
    @Transactional
    public BookingResponseDto create(Long userId, BookingRequestDto request) {
        if (!request.checkInDate().isBefore(request.checkOutDate())) {
            throw new InvalidDateRangeException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        List<RoomAvailability> availabilities = roomAvailabilityService.getAvailableRoomsForUpdate(request.hotelId(),
                request.checkInDate(), request.checkOutDate(), request.roomsRequested(), request.roomTypeId());

        int daysAvailabilities = availabilities.size();

        long daysReserved = ChronoUnit.DAYS.between(request.checkInDate(), request.checkOutDate());

        if (daysAvailabilities != daysReserved) {
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
        booking.setUserId(userId);
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
     * @param Long userId
     * @param Long bookingId
     * @return BookingResponseDto
     */
    public BookingResponseDto findById(Long userId, Long bookingId) {

        if (bookingId == null || bookingId < 1) {
            throw new BookingNotFoundException(BOOKING_NOT_FOUND + bookingId);
        }

        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(BOOKING_NOT_FOUND + bookingId));

        if (!booking.getUserId().equals(userId)) {
            throw new BookingAccessDeniedException(ACCESS_DENIED);
        }

        return mapper.toResponseDto(booking);
    }

    /**
     * Devuelve todas las reservas del usuario
     * 
     * @param pageable
     * @return
     */
    public Page<BookingResponseDto> findAll(Pageable pageable, Long userId) {
        return repository.findAllByUserId(userId, pageable).map(mapper::toResponseDto);
    }

    /**
     * Devuelve una reserva con los datos del hotel y sus reseñas
     * 
     * @param Long id -> bookingId
     * @return BookingDetailResponseDto
     */
    public BookingDetailResponseDto getBookingDetails(Long id, Long userId) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(BOOKING_NOT_FOUND + id));

        if (!booking.getUserId().equals(userId)) {
            throw new BookingAccessDeniedException(ACCESS_DENIED);
        }

        HotelSummaryDto hotelSummaryDto = externalHotelService.getHotel(booking.getHotelId());

        List<ReviewSummaryDto> reviewSummaryDto = externalHotelService.getReviews(booking.getHotelId());

        return new BookingDetailResponseDto(mapper.toResponseDto(booking), hotelSummaryDto, reviewSummaryDto);
    }

    /**
     * Cambia el estado de la reserva a confirmado
     * 
     * @param Long bookingId
     * @return BookingResponseDto
     */
    @Transactional
    public BookingResponseDto confirmBooking(Long bookingId) {
        Booking booking = repository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(BOOKING_NOT_FOUND + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidBookingStateException("El estado de la reserva no permite la confirmación");
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        return mapper.toResponseDto(repository.save(booking));
    }

    /**
     * Cancela una reserva, cambia su estado a cancelado. Para obtener todos los
     * resultados envío al repositorio 0 en roomsRequested
     * 
     * @param Long bookingId
     * @return BookingResponseDto
     */
    @Transactional
    public BookingResponseDto cancelBooking(Long bookingId) {
        Booking booking = repository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(BOOKING_NOT_FOUND + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingStateException("La reserva ya ha sido cancelada");
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new InvalidBookingStateException("El estado de la reserva no permite la cancelación");
        }

        List<RoomAvailability> availabilities = roomAvailabilityService.getAvailableRoomsForUpdate(booking.getHotelId(),
                booking.getCheckInDate(), booking.getCheckOutDate(), 0, booking.getRoomTypeId());

        for (RoomAvailability roomAvailability : availabilities) {
            roomAvailability
                    .setAvailableQuantity(roomAvailability.getAvailableQuantity() + booking.getRoomsRequested());
        }

        roomAvailabilityService.saveAllUpdated(availabilities);

        booking.setStatus(BookingStatus.CANCELLED);

        return mapper.toResponseDto(repository.save(booking));
    }
}
