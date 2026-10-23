package com.staybook.booking.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.staybook.booking.dto.event.BookingEventDto;
import com.staybook.booking.publisher.BookingEventPublisher;

@Component
public class BookingEventAfterCommitListener {

    private final BookingEventPublisher publisher;

    public BookingEventAfterCommitListener(BookingEventPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingEvent(BookingEventDto event) {
        publisher.publish(event);
    }
}