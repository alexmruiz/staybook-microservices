package com.staybook.booking.enums;

public enum BookingEventType {
    BOOKING_CONFIRMED("booking.confirmed"), 
    BOOKING_CANCELLED("booking.cancelled");

    private final String routingKey;

    BookingEventType(String routingKey) {
        this.routingKey = routingKey;
    }

    public String routingKey() {
        return routingKey;
    }
}
