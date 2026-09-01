package com.pamir.ppfarmsbackend.shared.exception;

public class PlanQuotaExceededException extends RuntimeException {
    public PlanQuotaExceededException(String message) {
        super(message);
    }
}
