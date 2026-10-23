package com.staybook.booking.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import com.staybook.booking.dto.event.BookingEventDto;
import com.staybook.booking.enums.BookingEventType;
import com.staybook.booking.publisher.BookingEventPublisher;

@SpringJUnitConfig(BookingEventAfterCommitListenerTest.TestConfig.class)
class BookingEventAfterCommitListenerTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private BookingEventPublisher publisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void resetPublisher() {
        reset(publisher);
    }

    @Test
    void onBookingEvent_shouldPublishAfterTransactionCommit() {
        BookingEventDto event = createEvent();
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);

        transaction.executeWithoutResult(status -> {
            applicationEventPublisher.publishEvent(event);
            verify(publisher, never()).publish(event);
        });

        verify(publisher).publish(event);
    }

    @Test
    void onBookingEvent_shouldNotPublishWhenTransactionRollsBack() {
        BookingEventDto event = createEvent();
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);

        transaction.executeWithoutResult(status -> {
            applicationEventPublisher.publishEvent(event);
            status.setRollbackOnly();
        });

        verify(publisher, never()).publish(event);
    }

    @Test
    void onBookingEvent_shouldNotPublishWithoutActiveTransaction() {
        BookingEventDto event = createEvent();

        applicationEventPublisher.publishEvent(event);

        verify(publisher, never()).publish(event);
    }

    private BookingEventDto createEvent() {
        return new BookingEventDto(
                "event-123",
                BookingEventType.BOOKING_CONFIRMED,
                1,
                Instant.parse("2026-10-23T08:00:00Z"),
                42L,
                "SB-ABC12345",
                7L,
                LocalDate.of(2026, 11, 5),
                LocalDate.of(2026, 11, 7));
    }

    @Configuration
    @EnableTransactionManagement
    static class TestConfig {

        @Bean
        BookingEventAfterCommitListener bookingEventAfterCommitListener(BookingEventPublisher publisher) {
            return new BookingEventAfterCommitListener(publisher);
        }

        @Bean
        BookingEventPublisher bookingEventPublisher() {
            return mock(BookingEventPublisher.class);
        }

        @Bean
        DataSource dataSource() {
            EmbeddedDatabase database = new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
            return database;
        }

        @Bean
        PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }
}
