package com.hotelsbook.reviews.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReviewRequestDto(
                @NotNull @Positive Long hotelId,

                @NotNull @Positive Long userId,

                @NotNull @Positive Double qualification,

                String description) {
}