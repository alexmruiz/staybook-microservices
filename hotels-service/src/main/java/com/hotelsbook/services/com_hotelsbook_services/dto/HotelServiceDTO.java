package com.hotelsbook.services.com_hotelsbook_services.dto;

import java.util.List;

import com.hotelsbook.services.com_hotelsbook_services.model.ServiceResponse;

public class HotelServiceDTO {


    private Long HotelId;
    private String hotelName;
    private List<ServiceResponse> service;


    public HotelServiceDTO(Long hotelId, String hotelName, List<ServiceResponse> service) {
        HotelId = hotelId;
        this.hotelName = hotelName;
        this.service = service;
    }


    public Long getHotelId() {
        return HotelId;
    }


    public void setHotelId(Long hotelId) {
        HotelId = hotelId;
    }


    public String getHotelName() {
        return hotelName;
    }


    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }


    public List<ServiceResponse> getService() {
        return service;
    }


    public void setService(List<ServiceResponse> service) {
        this.service = service;
    }

    

    

}
