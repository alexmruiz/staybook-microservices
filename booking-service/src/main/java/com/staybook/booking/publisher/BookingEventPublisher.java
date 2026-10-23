package com.staybook.booking.publisher;

import com.staybook.booking.dto.event.BookingEventDto;

public interface BookingEventPublisher {
    void publish(BookingEventDto event);

}
