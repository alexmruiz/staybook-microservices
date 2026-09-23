package com.staybook.booking.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookingRequestDto(
    @NotNull @Positive Long userId,
    @NotNull @Positive Long hotelId,
    @NotNull @Positive Long roomTypeId,
    @NotNull @FutureOrPresent LocalDate checkInDate,
    @NotNull @Future LocalDate checkOutDate
) {}