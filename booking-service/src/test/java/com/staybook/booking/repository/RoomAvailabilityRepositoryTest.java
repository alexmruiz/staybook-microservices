package com.staybook.booking.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;

import com.staybook.booking.entity.RoomAvailability;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RoomAvailabilityRepositoryTest {

    @Autowired
    private RoomAvailabilityRepository repository;

    @Test
    @Transactional
    void findAllForUpdate_returnsOrderedEntries() {
        Long hotelId = 100L;
        Long roomTypeId = 200L;
        LocalDate d1 = LocalDate.of(2026, 9, 20);
        LocalDate d2 = LocalDate.of(2026, 9, 21);

        RoomAvailability r1 = new RoomAvailability(1L, hotelId, roomTypeId, d1, 5, BigDecimal.valueOf(100));
        RoomAvailability r2 = new RoomAvailability(1L, hotelId, roomTypeId, d2, 3, BigDecimal.valueOf(120));

        repository.saveAll(List.of(r2, r1));
        repository.flush(); // ensure persisted

        List<LocalDate> dates = List.of(d1, d2);
        List<RoomAvailability> result = repository.findAllForUpdate(hotelId, roomTypeId, dates);

        assertEquals(2, result.size());
        assertEquals(d1, result.get(0).getDate());
        assertEquals(d2, result.get(1).getDate());
    }
}