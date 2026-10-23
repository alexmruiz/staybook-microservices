package com.staybook.booking.dto.event;

import java.time.LocalDate;

import com.staybook.booking.enums.BookingEventType;

import java.time.Instant;

public record BookingEventDto(
    String eventId,
    BookingEventType eventType,
    Integer version,
    Instant occurredAt,
    Long bookingId,
    String bookingReference,
    Long userId,
    LocalDate checkInDate,
    LocalDate checkOutDate
) {

}

