package com.staybook.auth.dto.response;

public record UserBookingResponseDto(
    Long id,
    String email,
    String name,
    String surname
) {
    
}
