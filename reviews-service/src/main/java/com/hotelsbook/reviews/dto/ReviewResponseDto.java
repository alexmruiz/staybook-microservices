package com.hotelsbook.reviews.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de respuesta con la información completa de la reseña")
public class ReviewResponseDto {

    @Schema(description = "Identificador único autogenerado de la reseña", example = "1")
    private Long id;

    @Schema(description = "Identificador único del hotel asociado", example = "101")
    private Long hotelId;

    @Schema(description = "Calificación promedio otorgada al hotel", example = "4.5")
    private Double averageCalification;

    @Schema(description = "Fecha y hora de creación de la reseña", example = "2026-08-24T17:30:00")
    private LocalDateTime createdAt;

    public ReviewResponseDto() {
    }

    public ReviewResponseDto(Long id, Long hotelId, Double averageCalification, LocalDateTime createdAt) {
        this.id = id;
        this.hotelId = hotelId;
        this.averageCalification = averageCalification;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}