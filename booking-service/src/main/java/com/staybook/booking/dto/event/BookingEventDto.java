package com.staybook.booking.dto.event;

import java.time.LocalDate;

import com.staybook.booking.enums.BookingStatus;

import java.time.Instant;

public record BookingEventDto(
    String eventId,
    BookingStatus eventType,
    Integer version,
    Instant occurredAt,
    Long bookingId,
    String bookingReference,
    Long userId,
    LocalDate checkInDate,
    LocalDate checkOutDate
) {

}

