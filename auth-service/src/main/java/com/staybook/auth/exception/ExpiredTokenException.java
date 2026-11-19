package com.staybook.auth.exception;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException (String message) {
        super(message);
    }
}
