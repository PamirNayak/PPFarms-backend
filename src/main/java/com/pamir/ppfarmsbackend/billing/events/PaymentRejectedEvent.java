package com.pamir.ppfarmsbackend.billing.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class PaymentRejectedEvent extends ApplicationEvent {

    private final UUID paymentId;
    private final UUID organizationId;
    private final String reason;
    private final BigDecimal amount;
    private final String transactionRef;
    private final String planName;

    public PaymentRejectedEvent(Object source, UUID paymentId, UUID organizationId, String reason, BigDecimal amount, String transactionRef, String planName) {
        super(source);
        this.paymentId = paymentId;
        this.organizationId = organizationId;
        this.reason = reason;
        this.amount = amount;
        this.transactionRef = transactionRef;
        this.planName = planName;
    }
}