package com.hotelsbook.reviews.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotelId", nullable = false)
    private Long hotelId;

    @Column(name = "average_calification", nullable = false)
    private Double averageCalification;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ReviewEntity() {
    }

    public ReviewEntity(Long hotelId, Double averageCalification) {
        this.hotelId = hotelId;
        this.averageCalification = averageCalification;
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}