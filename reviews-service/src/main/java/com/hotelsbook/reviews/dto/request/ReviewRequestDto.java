package com.hotelsbook.reviews.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReviewRequestDto(
        @NotNull @Positive Long hotelId,

        @NotNull @Positive Long userId,

        @DecimalMin(value = "0.0", inclusive = true) 
        @DecimalMax(value = "5.0", inclusive = true) 
        Double qualification,

        String description) {
}