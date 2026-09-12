package com.pamir.ppfarmsbackend.sales.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SalePaymentStatus {
    PAID("Paid in Full"),
    PENDING("Payment Pending"),
    PARTIAL("Partially Paid"),
    CANCELLED("Cancelled");

    private final String displayName;
}
