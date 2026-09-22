package com.staybook.booking.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.staybook.booking.entity.RoomAvailability;

import jakarta.persistence.LockModeType;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
      SELECT r FROM RoomAvailability r
      WHERE r.hotelId = :hotelId
        AND r.roomTypeId = :roomTypeId
        AND r.date IN :dates
      ORDER BY r.date
      """)
  List<RoomAvailability> findAllForUpdate(Long hotelId, Long roomTypeId, List<LocalDate> dates);

  @Query("""
      SELECT r FROM RoomAvailability r
      WHERE r.hotelId = :hotelId
        AND r.date >= :startDate
        AND r.date < :endDate
        AND r.availableQuantity >= :availableQuantity
        AND r.roomTypeId = :roomTypeId
      ORDER BY r.date
      """)
  List<RoomAvailability> getAvailableRooms(
      Long hotelId,
      LocalDate startDate,
      LocalDate endDate,
      int availableQuantity,
      Long roomTypeId
    );
}
