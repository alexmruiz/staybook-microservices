package com.hotelsbook.services.com_hotelsbook_services.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;


@Entity
@Table(name = "hotels")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "address_id", nullable = false, unique = true)
    private Address address;

    @Column(nullable = false)
    private Integer stars;

    private Integer capacity;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoomType> roomTypes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "hotel_services",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Amenity> services = new HashSet<>();

    protected Hotel() {
    }

    public Hotel(String name, String description, Address address, Integer stars, Integer capacity) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.stars = stars;
        this.capacity = capacity;
    }

    // --- getters y setters ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Set<RoomType> getRoomTypes() {
        return roomTypes;
    }

    public Set<Amenity> getServices() {
        return services;
    }

    // Métodos de conveniencia para mantener la relación bidireccional coherente
    public void addRoomType(RoomType roomType) {
        roomTypes.add(roomType);
        roomType.setHotel(this);
    }

    public void removeRoomType(RoomType roomType) {
        roomTypes.remove(roomType);
        roomType.setHotel(null);
    }

    public void addService(Amenity service) {
        services.add(service);
        service.getHotels().add(this);
    }
}