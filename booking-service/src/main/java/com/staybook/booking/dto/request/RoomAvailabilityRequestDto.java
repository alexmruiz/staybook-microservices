package com.staybook.booking.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record RoomAvailabilityRequestDto(
    @NotNull @Positive Long hotelId,
    @NotNull @Positive Long roomTypeId,
    @NotNull LocalDate date,
    @NotNull @PositiveOrZero Integer availableQuantity,
    @NotNull @DecimalMin (value = "0.0", inclusive = true) BigDecimal totalPrice
) {
} 
