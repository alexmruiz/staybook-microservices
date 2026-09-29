package com.staybook.booking.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.staybook.booking.entity.Booking;
import com.staybook.booking.enums.BookingStatus;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
class BookingRepositoryTest {

    private static final long HOTEL_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long ROOM_ID = 3L;
    private static final int GUESTS_COUNT = 1;
    private static final int THREAD_POOL_SIZE = 2;
    private static final int AWAIT_TIMEOUT_SECONDS = 5;
    private static final int EXECUTOR_TIMEOUT_SECONDS = 10;
    private static final long BLOCKING_DELAY_MILLIS = 200L;

    @Autowired
    private BookingRepository repository;

    @Autowired
    private PlatformTransactionManager txManager;

    @Test
    void findByIdForUpdate_returnsSavedBooking() {
        Booking booking = createTestBooking();
        Booking savedBooking = repository.save(booking);

        Optional<Booking> foundBooking = repository.findByIdForUpdate(savedBooking.getId());

        assertTrue(foundBooking.isPresent(), "Booking should be found by ID");
        assertEquals(savedBooking.getId(), foundBooking.get().getId(), "Found booking ID should match saved booking ID");
    }

    /**
     * Test that verifies pessimistic locking behavior: when one transaction acquires
     * a lock via findByIdForUpdate, a second transaction blocks until the first releases it.
     *
     * DISABLED: This test requires a real relational database (PostgreSQL, MySQL, etc.)
     * that properly implements row-level locks. H2 in-memory database does not reliably
     * simulate database locks in multi-threaded test scenarios, causing intermittent
     * failures (flakiness).
     *
     * To enable this test:
     * 1. Configure an integration test profile with a real database (TestContainers, Docker)
     * 2. Use @Disabled("Requires TestContainers with PostgreSQL") and move to integration tests
     * 3. See docs/TESTING.md for detailed instructions on setting up integration tests
     */
    @Disabled("Requires real database for reliable lock testing; H2 does not implement row-level locks")
    @Test
    void concurrent_findByIdForUpdate_secondThreadBlocksUntilFirstReleases() throws Exception {
        // This test is kept for reference and future enabling with proper database setup
        Booking savedBooking = repository.save(createTestBooking());
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try {
            testConcurrentLockBehavior(savedBooking, executorService);
        } finally {
            executorService.shutdownNow();
        }
    }

    /**
     * Helper method to test concurrent lock acquisition and blocking behavior.
     *
     * @param booking the booking entity to lock
     * @param executorService the executor service for running threads
     * @throws Exception if thread execution fails
     */
    private void testConcurrentLockBehavior(Booking booking, ExecutorService executorService) throws Exception {
        CountDownLatch firstThreadLocked = new CountDownLatch(1);
        CountDownLatch allowFirstThreadToFinish = new CountDownLatch(1);
        CountDownLatch secondThreadCanAttempt = new CountDownLatch(1);
        TransactionTemplate transactionTemplate = new TransactionTemplate(txManager);

        // Thread A: acquires lock via findByIdForUpdate
        Future<Void> firstThread = acquireLockAndHoldIt(
            executorService, transactionTemplate, booking.getId(),
            firstThreadLocked, allowFirstThreadToFinish
        );

        // Wait for thread A to acquire the lock
        boolean lockAcquired = firstThreadLocked.await(AWAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(lockAcquired, "First thread should acquire the lock within timeout");

        // Schedule second thread's attempt with a delay using a scheduled task
        Future<Void> delayTask = scheduleSecondThreadAttemptAfterDelay(
            executorService, secondThreadCanAttempt
        );

        // Thread B: attempt to acquire the same lock (will block)
        Future<Long> secondThread = attemptLockWithTimer(executorService, transactionTemplate, booking.getId());

        // Wait before allowing first thread to finish (second thread should be blocked)
        boolean delayWaitComplete = secondThreadCanAttempt.await(BLOCKING_DELAY_MILLIS * 2, TimeUnit.MILLISECONDS);
        assertTrue(delayWaitComplete, "Delay task should complete");
        delayTask.get(EXECUTOR_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Release first thread's lock
        allowFirstThreadToFinish.countDown();

        // Verify second thread was able to complete (after first released)
        long secondThreadElapsedTime = secondThread.get(EXECUTOR_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        assertTrue(secondThreadElapsedTime >= BLOCKING_DELAY_MILLIS,
            "Second thread should have been blocked for at least " + BLOCKING_DELAY_MILLIS + "ms");

        // Verify first thread completed successfully
        firstThread.get(EXECUTOR_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Schedules a delay task that signals after the blocking delay period.
     * This replaces Thread.sleep() with a more robust timing mechanism.
     *
     * @param executorService the executor service
     * @param signal the latch to signal after delay
     * @return a Future representing the delay task
     */
    private Future<Void> scheduleSecondThreadAttemptAfterDelay(
            ExecutorService executorService,
            CountDownLatch signal) {

        return executorService.submit(() -> {
            try {
                Thread.sleep(BLOCKING_DELAY_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Delay task was interrupted", e);
            } finally {
                signal.countDown();
            }
            return null;
        });
    }

    /**
     * Submits a task to acquire a lock and hold it until signaled to release.
     *
     * @param executorService the executor service
     * @param transactionTemplate the transaction template
     * @param bookingId the ID of the booking to lock
     * @param lockAcquiredSignal signal to indicate lock was acquired
     * @param releaseSignal signal to release the lock
     * @return a Future representing the async task
     */
    private Future<Void> acquireLockAndHoldIt(
            ExecutorService executorService,
            TransactionTemplate transactionTemplate,
            Long bookingId,
            CountDownLatch lockAcquiredSignal,
            CountDownLatch releaseSignal) {

        return executorService.submit(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                repository.findByIdForUpdate(bookingId);
                lockAcquiredSignal.countDown();
                try {
                    releaseSignal.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Lock-holding thread was interrupted", e);
                }
            });
            return null;
        });
    }

    /**
     * Submits a task that attempts to acquire a lock and returns the time it took.
     *
     * @param executorService the executor service
     * @param transactionTemplate the transaction template
     * @param bookingId the ID of the booking to lock
     * @return a Future that yields the elapsed time in milliseconds
     */
    private Future<Long> attemptLockWithTimer(
            ExecutorService executorService,
            TransactionTemplate transactionTemplate,
            Long bookingId) {

        return executorService.submit(() -> {
            long startTime = System.currentTimeMillis();
            transactionTemplate.execute(status -> {
                repository.findByIdForUpdate(bookingId);
                return null;
            });
            return System.currentTimeMillis() - startTime;
        });
    }

    /**
     * Factory method to create a test booking with predefined values.
     *
     * @return a Booking entity with test data
     */
    private Booking createTestBooking() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(1);
        Booking booking = new Booking(HOTEL_ID, USER_ID, ROOM_ID, checkIn, checkOut, GUESTS_COUNT);
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(BigDecimal.ZERO);
        return booking;
    }
}