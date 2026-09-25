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

        RoomAvailability r1 = new RoomAvailability(hotelId, roomTypeId, d1, 5, BigDecimal.valueOf(100));
        RoomAvailability r2 = new RoomAvailability(hotelId, roomTypeId, d2, 3, BigDecimal.valueOf(120));

        repository.saveAll(List.of(r2, r1));
        repository.flush(); // ensure persisted

        List<RoomAvailability> result = repository.findAllForUpdate(hotelId, d1, d2, 2, roomTypeId);

        assertEquals(2, result.size());
        assertEquals(d1, result.get(0).getDate());
        assertEquals(d2, result.get(1).getDate());
    }

    @Test
    @Transactional
    void getAvailableRooms_filtersByDateAndQuantity_andOrdersByDate() {
        Long hotelId = 1L;
        Long roomTypeId = 10L;

        LocalDate d1 = LocalDate.of(2026, 9, 20);
        LocalDate d2 = LocalDate.of(2026, 9, 21);
        LocalDate d3 = LocalDate.of(2026, 9, 22);

        // r2 has availableQuantity < requested and should be filtered out
        RoomAvailability r1 = new RoomAvailability(hotelId, roomTypeId, d1, 3, BigDecimal.valueOf(100));
        RoomAvailability r2 = new RoomAvailability(hotelId, roomTypeId, d2, 1, BigDecimal.valueOf(110));
        RoomAvailability r3 = new RoomAvailability(hotelId, roomTypeId, d3, 4, BigDecimal.valueOf(120));

        repository.saveAll(List.of(r1, r2, r3));
        repository.flush();

        LocalDate start = d1;
        LocalDate endExclusive = d3.plusDays(1); // query uses date < endDate (exclusive)
        Integer requestedQuantity = 2;

        List<RoomAvailability> result = repository.getAvailableRooms(hotelId, start, endExclusive, requestedQuantity, roomTypeId);

        assertEquals(2, result.size(), "Debe devolver solo d1 y d3 (d2 tiene < requested)");
        assertEquals(d1, result.get(0).getDate());
        assertEquals(d3, result.get(1).getDate());
        assertTrue(result.stream().allMatch(r -> r.getAvailableQuantity() >= requestedQuantity));
    }
}