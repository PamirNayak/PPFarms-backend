package com.pamir.ppfarmsbackend.billing.service;

import com.pamir.ppfarmsbackend.billing.dto.PaymentResponse;
import com.pamir.ppfarmsbackend.billing.dto.PaymentSubmitRequest;
import com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse submitPaymentProof(PaymentSubmitRequest request, UUID tenantId);
    PaymentResponse approvePayment(UUID paymentId, UUID adminUserId);
    PaymentResponse rejectPayment(UUID paymentId, String reason, UUID adminUserId);
    List<PaymentResponse> getPendingPayments();
    List<PaymentResponse> getFarmPaymentHistory(UUID tenantId);

    SubscriptionResponse getFarmSubscription(UUID tenantId);
    SubscriptionResponse claimFreeTrial(UUID tenantId);
}
