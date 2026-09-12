package com.pamir.ppfarmsbackend.billing.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class PaymentApprovedEvent extends ApplicationEvent {

    private final UUID paymentId;
    private final UUID organizationId;
    private final UUID subscriptionId;
    private final String invoiceNumber;
    private final String planName;
    private final String planType;
    private final BigDecimal amount;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String transactionRef;

    public PaymentApprovedEvent(Object source, UUID paymentId, UUID organizationId, UUID subscriptionId,
                                String invoiceNumber, String planName, String planType, BigDecimal amount,
                                LocalDate startDate, LocalDate endDate, String transactionRef) {
        super(source);
        this.paymentId = paymentId;
        this.organizationId = organizationId;
        this.subscriptionId = subscriptionId;
        this.invoiceNumber = invoiceNumber;
        this.planName = planName;
        this.planType = planType;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.transactionRef = transactionRef;
    }
}