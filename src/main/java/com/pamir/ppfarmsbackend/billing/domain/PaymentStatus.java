package com.pamir.ppfarmsbackend.billing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING_VERIFICATION("Pending Verification"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String displayName;
}
