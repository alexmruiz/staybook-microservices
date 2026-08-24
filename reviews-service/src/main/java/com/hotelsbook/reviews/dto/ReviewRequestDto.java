package com.hotelsbook.reviews.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReviewRequestDto {

    @NotNull
    @Positive
    private Long hotelId;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("5.0")
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
