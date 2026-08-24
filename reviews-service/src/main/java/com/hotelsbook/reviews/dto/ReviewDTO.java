package com.hotelsbook.reviews.dto;

public class ReviewDTO {

    private Long hotelId;
    private Double qualification;

    public ReviewDTO(Long hotelId, Double qualification) {
        this.hotelId = hotelId;
        this.qualification = qualification;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Double getQualification() {
        return qualification;
    }

    public void setQualification(Double qualification) {
        this.qualification = qualification;
    }
}
