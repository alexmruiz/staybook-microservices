package com.staybook.auth.dto.response;

import java.time.LocalDateTime;

import com.staybook.auth.enums.TypeRole;

public record AuthResponseDto(
    Long id,
    String email,
    String name,
    String surname,
    TypeRole role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
} 
