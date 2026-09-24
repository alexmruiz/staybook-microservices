package com.hotelsbook.reviews.dto.response;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long id,
        Long hotelId,
        Long userId,
        Double qualification,
        LocalDateTime createdAt) {
}