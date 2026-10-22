package com.staybook.notifications.dto;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.staybook.notifications.enums.BookingStatus;

public record BookingEventDto(
        @NotNull String eventId,
        @NotNull BookingStatus eventType,
        @NotNull @Positive  Integer version,
        @NotNull Instant occurredAt,
        @NotNull @Positive Long bookingId,
        @NotNull String bookingReference,
        @NotNull @Positive Long userId,
        @NotNull LocalDate checkInDate,
        @NotNull LocalDate checkOutDate
) {
}
