package com.staybook.auth.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.staybook.auth.dto.response.AuthEventDto;
import com.staybook.auth.publisher.AuthEventPublisher;

@Component
public class AuthEventAfterCommitListener {

    private final AuthEventPublisher publisher;

    public AuthEventAfterCommitListener(AuthEventPublisher publisher) {
        this.publisher = publisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAuthEvent(AuthEventDto event) {
        publisher.publish(event);
    }
}
