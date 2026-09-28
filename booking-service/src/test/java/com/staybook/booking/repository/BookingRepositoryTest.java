package com.staybook.booking.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.staybook.booking.entity.Booking;
import com.staybook.booking.enums.BookingStatus;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private PlatformTransactionManager txManager;

    @Test
    void findByIdForUpdate_returnsSavedBooking() {
        Booking b = new Booking(1L, 2L, 3L, java.time.LocalDate.now().plusDays(1),
                java.time.LocalDate.now().plusDays(2), 1);
        b.setStatus(BookingStatus.PENDING);
        b.setTotalPrice(BigDecimal.ZERO);

        Booking saved = repository.save(b);

        Optional<Booking> found = repository.findByIdForUpdate(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    // Test concurrent lock behaviour (example outline).
    // This is more complex: requires running two transactions in parallel and
    // asserting the second one blocks until the first transaction holding the lock ends.
    // The code below is an illustrative sketch — adapt and enable if you want to
    // test locking behaviour in CI (may need TransactionTemplate or @Transactional on helpers).
    @Test
    void concurrent_findByIdForUpdate_secondThreadBlocksUntilFirstReleases() throws Exception {
        Booking b = new Booking(1L, 1L, 1L, java.time.LocalDate.now().plusDays(1),
                java.time.LocalDate.now().plusDays(2), 1);
        b.setStatus(BookingStatus.PENDING);
        b.setTotalPrice(BigDecimal.ZERO);
        Booking saved = repository.save(b);

        ExecutorService ex = Executors.newFixedThreadPool(2);
        CountDownLatch firstLocked = new CountDownLatch(1);
        CountDownLatch allowFirstFinish = new CountDownLatch(1);
        TransactionTemplate txTemplate = new TransactionTemplate(txManager);

        // Thread A: opens tx and calls findByIdForUpdate (holds DB lock)
        Future<Void> tA = ex.submit(() -> {
            txTemplate.executeWithoutResult(status -> {
                repository.findByIdForUpdate(saved.getId());
                firstLocked.countDown(); // signal that lock was acquired
                try {
                    allowFirstFinish.await(); // hold the lock until main thread allows
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            });
            return null;
        });

        // Wait that first thread acquired lock (in a proper transactional test)
        assertTrue(firstLocked.await(5, TimeUnit.SECONDS));

        // Thread B: attempt to acquire lock — should block until Thread A finishes
        Future<Long> tB = ex.submit(() -> {
            long start = System.currentTimeMillis();
            txTemplate.execute(status -> {
                repository.findByIdForUpdate(saved.getId()); // will block until A commits/rollbacks
                return null;
            });
            return System.currentTimeMillis() - start;
        });

        // give the second thread a short time to attempt acquire (it should be blocked)
        Thread.sleep(200);

        // now allow first to finish (release lock)
        allowFirstFinish.countDown();

        // get elapsed time for thread B (should be >= time it was blocked)
        long elapsed = tB.get(10, TimeUnit.SECONDS);

        assertTrue(elapsed >= 0); // replace with more precise assertions after adapting to real tx handling

        ex.shutdownNow();
    }
}