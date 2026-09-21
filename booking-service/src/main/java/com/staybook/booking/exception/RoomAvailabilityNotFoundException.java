package com.staybook.booking.exception;

public class RoomAvailabilityNotFoundException extends RuntimeException {

    public RoomAvailabilityNotFoundException(String message) {
        super(message);
    }
    
}
