package com.staybook.booking.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.staybook.booking.repository.BookingRepository;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.entity.Booking;
import com.staybook.booking.enums.BookingStatus;
import com.staybook.booking.exception.BookingNotFoundException;
import com.staybook.booking.exception.InvalidDateRangeException;
import com.staybook.booking.mapper.BookingMapper;
import com.staybook.booking.dto.request.BookingRequestDto;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private static final String BOOKING_NOT_FOUND = "Reserva no encontrada con id: ";

    public BookingService(BookingRepository repository, BookingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Crea una reserva
     * 
     * @param BookingRequestDto request
     * @return BookingResponseDto response
     */
    public BookingResponseDto create(BookingRequestDto request) {
        if (!request.checkInDate().isBefore(request.checkOutDate())) {
            throw new InvalidDateRangeException("La fecha de salida debe ser posterior a la fecha de entrada");
        }

        Booking booking = mapper.toEntity(request);

        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(BigDecimal.valueOf(100.00));// TODO(BOOK-07): calcular sumando RoomAvailability.price para
                                                          // cada día del rango
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
}
