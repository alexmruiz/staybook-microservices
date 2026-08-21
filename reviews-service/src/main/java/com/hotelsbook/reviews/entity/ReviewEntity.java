package com.hotelsbook.reviews.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ReviewEntity {

    @Id
    private Long hotelId;
    private String hotelName;
    private Double averageCalification;

    public ReviewEntity(Long hotelId, String hotelName, Double averageCalification) {
        super();
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.averageCalification = averageCalification;
    }

    public Long getHotelId() {
        return hotelId;
    }
    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
    public String getHotelName() {
        return hotelName;
    }
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
    public Double getAverageCalification() {
        return averageCalification;
    }
    public void setAverageCalification(Double averageCalification) {
        this.averageCalification = averageCalification;
    }

}
