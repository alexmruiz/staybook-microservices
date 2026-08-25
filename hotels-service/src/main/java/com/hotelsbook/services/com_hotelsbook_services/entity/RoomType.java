package com.hotelsbook.services.com_hotelsbook_services.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "room_types", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"hotel_id", "type"})
})
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomTypeName type;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    protected RoomType() {
    }

    public RoomType(RoomTypeName type, Integer quantity, Hotel hotel) {
        this.type = type;
        this.quantity = quantity;
        this.hotel = hotel;
    }

    public Long getId() {
        return id;
    }

    public RoomTypeName getType() {
        return type;
    }

    public void setType(RoomTypeName type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }
}