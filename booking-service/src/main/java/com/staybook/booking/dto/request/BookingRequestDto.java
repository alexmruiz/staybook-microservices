package com.staybook.booking.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.staybook.booking.enums.BookingStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookingRequestDto(
    @NotNull @Positive Long userId,
    @NotNull @Positive Long hotelId,
    @NotNull @Positive Long roomTypeId,
    @NotNull @FutureOrPresent LocalDate checkInDate,
    @NotNull @Future LocalDate checkOutDate,
    @NotNull BookingStatus status,
    @NotNull @DecimalMin (value = "0.0", inclusive = true) BigDecimal totalPrice
) {}