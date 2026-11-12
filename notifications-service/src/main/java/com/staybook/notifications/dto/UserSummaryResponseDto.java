package com.staybook.notifications.dto;

public record UserSummaryResponseDto(
    Long id,
    String email,
    String name,
    String surname
) {
    
}
