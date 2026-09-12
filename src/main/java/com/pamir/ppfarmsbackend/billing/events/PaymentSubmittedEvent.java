package com.pamir.ppfarmsbackend.billing.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class PaymentSubmittedEvent extends ApplicationEvent {

    private final UUID paymentId;
    private final UUID organizationId;
    private final String planName;
    private final BigDecimal amount;
    private final String paymentMethod;
    private final String transactionRef;

    public PaymentSubmittedEvent(Object source, UUID paymentId, UUID organizationId, String planName, BigDecimal amount, String paymentMethod, String transactionRef) {
        super(source);
        this.paymentId = paymentId;
        this.organizationId = organizationId;
        this.planName = planName;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionRef = transactionRef;
    }
}