package com.hotelsbook.services.com_hotelsbook_services.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class HotelServiceEntity {

    @Id
    private Long hotelId;
    private String hotelName;
    private Long serviceId;
    private String serviceName;

    public HotelServiceEntity() {}

    public HotelServiceEntity(Long hotelId, String hotelName, Long serviceId, String serviceName) {
        this.hotelId = hotelId;
        this.hotelName = hotelName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
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


    public Long getServiceId() {
        return serviceId;
    }


    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }


    public String getServiceName() {
        return serviceName;
    }


    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    

    

}
