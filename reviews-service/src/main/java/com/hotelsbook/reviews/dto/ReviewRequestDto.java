package com.hotelsbook.reviews.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "DTO de petición para crear o actualizar una reseña")
public class ReviewRequestDto {

    @Schema(description = "Identificador único del hotel", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El ID del hotel no puede ser nulo")
    @Positive(message = "El ID del hotel debe ser un número positivo")
    private Long hotelId;

    @Schema(description = "Calificación promedio del hotel (de 0.0 a 5.0)", example = "4.5", minimum = "0.0", maximum = "5.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La calificación es obligatoria")
    @DecimalMin(value = "0.0", message = "La calificación mínima permitida es 0.0")
    @DecimalMax(value = "5.0", message = "La calificación máxima permitida es 5.0")
    private Double averageCalification;

    public ReviewRequestDto() {
    }

    public ReviewRequestDto(Long hotelId, Double averageCalification) {
        this.hotelId = hotelId;
        this.averageCalification = averageCalification;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Double getAverageCalification() {
        return averageCalification;
    }

    public void setAverageCalification(Double averageCalification) {
        this.averageCalification = averageCalification;
    }
}