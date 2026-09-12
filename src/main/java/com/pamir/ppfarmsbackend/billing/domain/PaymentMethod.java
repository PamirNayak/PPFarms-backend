package com.pamir.ppfarmsbackend.billing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    UPI_QR("UPI QR Code"),
    BANK_TRANSFER("Direct Bank Transfer"),
    CASH("Cash Settlement"),
    ONLINE("Online Gateway");

    private final String displayName;
}
