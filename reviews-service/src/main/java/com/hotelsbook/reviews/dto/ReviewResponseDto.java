package com.hotelsbook.reviews.dto;

import java.time.LocalDateTime;

public class ReviewResponseDto {

    private Long id;
    private Long hotelId;
    private Double averageCalification;
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
