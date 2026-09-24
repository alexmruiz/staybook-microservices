package com.staybook.booking.dto.response;

public record ReviewSummaryDto(
    Long hotelId,
    Long userId,
    Double qualification,
    String description
) {
    
}
