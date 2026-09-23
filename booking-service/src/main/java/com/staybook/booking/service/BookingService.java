package com.staybook.booking.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.staybook.booking.repository.BookingRepository;
import com.staybook.booking.dto.response.BookingResponseDto;
import com.staybook.booking.entity.Booking;
import com.staybook.booking.enums.BookingStatus;
import com.staybook.booking.mapper.BookingMapper;
import com.staybook.booking.dto.request.BookingRequestDto;

@Service
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;

    public BookingService(BookingRepository repository, BookingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Crea una reserva
     * @param BookingRequestDto request
     * @return BookingResponseDto response
     */
    public BookingResponseDto create(BookingRequestDto request) {
        Booking booking = mapper.toEntity(request);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalPrice(BigDecimal.valueOf(100.00));
        booking.setBookingReference(this.generateBookingReference());
        Booking saved = repository.save(booking);
        return mapper.toResponseDto(saved);
    }

    /**
     * Genera un número único para cada reserva
     * @return string
     */
    private String generateBookingReference() {
        var uuuid = UUID.randomUUID().toString();
        var date = LocalDate.now(ZoneId.of("UTC")).toString();
        return "SB" + "-" + uuuid + "-" + date; 
    }
}
