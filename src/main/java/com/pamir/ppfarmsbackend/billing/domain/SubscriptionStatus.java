package com.pamir.ppfarmsbackend.billing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionStatus {
    UNCLAIMED("Unclaimed"),
    TRIAL("Free Trial"),
    ACTIVE("Active"),
    EXPIRED("Expired"),
    PENDING_APPROVAL("Pending Verification");

    private final String displayName;
}
