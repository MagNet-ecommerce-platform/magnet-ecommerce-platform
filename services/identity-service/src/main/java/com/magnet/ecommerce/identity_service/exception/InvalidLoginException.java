package com.magnet.ecommerce.identity_service.exception;

public class InvalidLoginException extends RuntimeException {

    private final int failedAttempts;
    private final int attemptsRemaining;

    public InvalidLoginException(
            String message,
            int failedAttempts,
            int attemptsRemaining) {

        super(message);
        this.failedAttempts = failedAttempts;
        this.attemptsRemaining = attemptsRemaining;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public int getAttemptsRemaining() {
        return attemptsRemaining;
    }
}