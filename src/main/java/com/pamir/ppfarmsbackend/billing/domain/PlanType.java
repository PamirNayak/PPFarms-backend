package com.pamir.ppfarmsbackend.billing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanType {
    FREE("Free Trial"),
    MONTHLY("Monthly Subscription"),
    ANNUAL("Annual Subscription");

    private final String displayName;
}
