package com.staybook.notifications.dto;

import java.time.Instant;
import java.time.LocalDate;

import com.staybook.notifications.enums.BookingEventType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookingEventDto(
        @NotBlank String eventId,
        @NotNull BookingEventType eventType,
        @NotNull @Positive Integer version,
        @NotNull Instant occurredAt,
        @NotNull @Positive Long bookingId,
        @NotBlank String bookingReference,
        @NotNull @Positive Long userId,
        @NotNull LocalDate checkInDate,
        @NotNull LocalDate checkOutDate
) {
}
