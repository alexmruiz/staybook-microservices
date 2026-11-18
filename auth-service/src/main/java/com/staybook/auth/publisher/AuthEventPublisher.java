package com.staybook.auth.publisher;

import com.staybook.auth.dto.response.AuthEventDto;

public interface AuthEventPublisher {
    void publish(AuthEventDto event);
}
