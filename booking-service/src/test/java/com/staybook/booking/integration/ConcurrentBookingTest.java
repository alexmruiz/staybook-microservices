package com.staybook.booking.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.staybook.booking.dto.request.BookingRequestDto;
import com.staybook.booking.entity.RoomAvailability;
import com.staybook.booking.exception.RoomNotAvailableException;
import com.staybook.booking.repository.BookingRepository;
import com.staybook.booking.repository.RoomAvailabilityRepository;
import com.staybook.booking.service.BookingService;

@SpringBootTest
@ActiveProfiles("test")
class ConcurrentBookingTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomAvailabilityRepository roomAvailabilityRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void concurrentBookings_ShouldNotOverbook() {
        // Arrange: one night with only 1 room available
        Long hotelId = 500L;
        Long roomTypeId = 600L;
        LocalDate date = LocalDate.now().plusDays(1);

        roomAvailabilityRepository.saveAll(List.of(
                new RoomAvailability(hotelId, roomTypeId, date, 1, BigDecimal.valueOf(100))
        ));

        BookingRequestDto req1 = new BookingRequestDto(1L, hotelId, roomTypeId, date, date.plusDays(1), 1);
        BookingRequestDto req2 = new BookingRequestDto(2L, hotelId, roomTypeId, date, date.plusDays(1), 1);

        ExecutorService exec = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        CompletableFuture<Boolean> f1 = CompletableFuture.supplyAsync(() -> {
            try {
                startLatch.await();
                bookingService.create(req1);
                return true;
            } catch (RoomNotAvailableException e) {
                return false;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, exec);

        CompletableFuture<Boolean> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                startLatch.await();
                bookingService.create(req2);
                return true;
            } catch (RoomNotAvailableException e) {
                return false;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, exec);

        // Start both at the same time
        startLatch.countDown();

        boolean r1 = f1.join();
        boolean r2 = f2.join();

        // Exactly one should succeed
        int successes = (r1 ? 1 : 0) + (r2 ? 1 : 0);
        assertEquals(1, successes, "Only one concurrent booking must succeed to avoid overbooking");

        // Cleanup
        exec.shutdownNow();
        bookingRepository.deleteAll();
        roomAvailabilityRepository.deleteAll();
    }
}
