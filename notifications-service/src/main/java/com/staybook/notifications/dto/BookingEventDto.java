package com.staybook.notifications.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.staybook.notifications.enums.BookingStatus;

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
