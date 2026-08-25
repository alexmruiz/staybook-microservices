package com.hotelsbook.reviews.dto.response;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta con la información completa de la reseña")
public record ReviewResponseDto(
        @Schema(description = "Identificador único autogenerado de la reseña", example = "1")
        Long id,
        @Schema(description = "Identificador único del hotel asociado", example = "101")
        Long hotelId,
        @Schema(description = "Calificación promedio otorgada al hotel", example = "4.5")
        Double qualification,
        @Schema(description = "Fecha y hora de creación de la reseña", example = "2026-08-24T17:30:00")
        LocalDateTime createdAt) {
}