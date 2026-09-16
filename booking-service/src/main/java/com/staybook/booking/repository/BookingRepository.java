package com.staybook.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.staybook.booking.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    
}
