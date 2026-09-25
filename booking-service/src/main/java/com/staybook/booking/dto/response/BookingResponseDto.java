package com.staybook.booking.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.staybook.booking.enums.BookingStatus;

public record BookingResponseDto(
    Long id,
    Long userId,
    Long hotelId,
    Long roomTypeId,
    LocalDate checkInDate,
    LocalDate checkOutDate,
    BookingStatus status,
    BigDecimal totalPrice,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String bookingReference,
    Integer roomsRequested
) {
    public BookingResponseDto(Long id, Long userId, Long hotelId, Long roomTypeId, LocalDate checkInDate,
            LocalDate checkOutDate, BookingStatus status, BigDecimal totalPrice, LocalDateTime createdAt,
            LocalDateTime updatedAt, String bookingReference) {
        this(id, userId, hotelId, roomTypeId, checkInDate, checkOutDate, status, totalPrice, createdAt, updatedAt,
                bookingReference, 1);
    }
}
