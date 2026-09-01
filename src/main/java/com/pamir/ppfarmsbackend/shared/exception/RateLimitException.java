package com.pamir.ppfarmsbackend.shared.exception;

public class RateLimitException extends RuntimeException {
    private final int limit;
    private final long resetSeconds;

    public RateLimitException(String message, int limit, long resetSeconds) {
        super(message);
        this.limit = limit;
        this.resetSeconds = resetSeconds;
    }

    public int getLimit() {
        return limit;
    }

    public long getResetSeconds() {
        return resetSeconds;
    }
}
