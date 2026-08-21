package com.hotelsbook.hotel.dto;

import java.util.List;

import com.hotelsbook.hotel.entity.HotelAvailable;

public class HotelAvailableDto {

    private Long id;

    private String name;

    private Double price;

    private String description;

    private String picture;

    private String street;

    private String number;

    private String cityName;

    private List<ServiceDto> services;

    private Double averageCalification;

    // Constructor
    public HotelAvailableDto() {
    }

    public HotelAvailableDto(HotelAvailable hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.price = hotel.getPrice();
        this.description = hotel.getDescription();
        this.picture = hotel.getPicture();
        this.street = hotel.getStreet();
        this.number = hotel.getNumber();
        this.cityName = hotel.getCityName();
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<ServiceDto> getServices() {
        return services;
    }

    public void setServices(List<ServiceDto> services) {
        this.services = services;
    }

    public Double getAverageCalification() {
        return averageCalification;
    }

    public void setAverageCalification(Double averageCalification) {
        this.averageCalification = averageCalification;
    }

}
