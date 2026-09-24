package com.staybook.booking.dto.response;

public record ReviewSumaryDto(
    Long hotelId,
    Long userId,
    Double qualification,
    String description
) {
    
}
