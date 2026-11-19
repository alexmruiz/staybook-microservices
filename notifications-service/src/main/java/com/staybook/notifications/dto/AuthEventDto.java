package com.staybook.notifications.dto;

import java.time.Instant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AuthEventDto(
    @NotBlank String eventId,
    @NotBlank String eventType,
    @Positive Integer version,
    @Email @NotBlank String email,
    @NotBlank String token,
    @NotNull Instant occurredAt
) {
    
}
