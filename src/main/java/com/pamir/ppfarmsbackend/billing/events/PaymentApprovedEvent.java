package com.pamir.ppfarmsbackend.billing.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class PaymentApprovedEvent extends ApplicationEvent {

    private final UUID paymentId;
    private final UUID organizationId;
    private final UUID subscriptionId;
    private final BigDecimal amount;

    public PaymentApprovedEvent(Object source, UUID paymentId, UUID organizationId, UUID subscriptionId, BigDecimal amount) {
        super(source);
        this.paymentId = paymentId;
        this.organizationId = organizationId;
        this.subscriptionId = subscriptionId;
        this.amount = amount;
    }

    public UUID getPaymentId() { return paymentId; }
    public UUID getOrganizationId() { return organizationId; }
    public UUID getSubscriptionId() { return subscriptionId; }
    public BigDecimal getAmount() { return amount; }
}
