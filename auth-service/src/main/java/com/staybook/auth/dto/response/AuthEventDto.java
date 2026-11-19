package com.staybook.auth.dto.response;

import java.time.Instant;

public record AuthEventDto(
    String eventId,
    String eventType,
    Integer version,
    String email,
    String token,
    Instant occurredAt
) {
    
}
