package com.hotelsbook.reviews.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReviewRequestDto(
                @NotNull(message = "El ID del hotel no puede ser nulo")
                @Positive(message = "El ID del hotel debe ser un número positivo")
                Long hotelId,

                @NotNull(message = "La calificación es obligatoria") 
                @DecimalMin(value = "0.0", message = "La calificación mínima permitida es 0.0") 
                @DecimalMax(value = "5.0", message = "La calificación máxima permitida es 5.0") 
                Double qualification
        ) {
}