package com.hotelsbook.reviews.dto;

public class ReviewDTO {

    private Long hotelId;
    private Double averageCalification;


    public ReviewDTO(Long hotelId, Double averageCalification) {
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
