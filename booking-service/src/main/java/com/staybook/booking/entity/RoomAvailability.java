package com.staybook.booking.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(
    name = "rooms_availability",
    indexes = { @Index(name = "idx_rooms_availability_hotel_id", columnList = "hotel_id") },
    uniqueConstraints = @UniqueConstraint(
        name = "uk_rooms_availability_hotel_room_date",
        columnNames = {"hotel_id", "room_type_id", "date"}
    )
)
    
public class RoomAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "room_type_id", nullable = false)
    private Long roomTypeId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public RoomAvailability() {
    }

    public RoomAvailability(Long hotelId, Long roomTypeId, LocalDate date, Integer availableQuantity,
            BigDecimal price) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.date = date;
        this.availableQuantity = availableQuantity;
        this.price = price;
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

    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
