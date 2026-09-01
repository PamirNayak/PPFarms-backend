package com.pamir.ppfarmsbackend.billing.service.impl;

import com.pamir.ppfarmsbackend.billing.dto.PaymentResponse;
import com.pamir.ppfarmsbackend.billing.dto.PaymentSubmitRequest;
import com.pamir.ppfarmsbackend.billing.entity.Invoice;
import com.pamir.ppfarmsbackend.billing.entity.Payment;
import com.pamir.ppfarmsbackend.billing.entity.Plan;
import com.pamir.ppfarmsbackend.billing.entity.Subscription;
import com.pamir.ppfarmsbackend.billing.events.PaymentApprovedEvent;
import com.pamir.ppfarmsbackend.billing.repository.InvoiceRepository;
import com.pamir.ppfarmsbackend.billing.repository.PaymentRepository;
import com.pamir.ppfarmsbackend.billing.repository.PlanRepository;
import com.pamir.ppfarmsbackend.billing.repository.SubscriptionRepository;
import com.pamir.ppfarmsbackend.billing.service.PaymentService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public PaymentResponse submitPaymentProof(PaymentSubmitRequest request, UUID tenantId) {
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        Subscription subscription = subscriptionRepository.findByOrganizationId(tenantId)
                .orElseGet(() -> {
                    Subscription newSub = Subscription.builder()
                            .organizationId(tenantId)
                            .plan(plan)
                            .status("PENDING_APPROVAL")
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now().plusDays(14))
                            .build();
                    return subscriptionRepository.save(newSub);
                });

        subscription.setPlan(plan);
        subscription.setStatus("PENDING_APPROVAL");
        subscriptionRepository.save(subscription);

        Payment payment = Payment.builder()
                .organizationId(tenantId)
                .subscriptionId(subscription.getId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionRef(request.getTransactionRef())
                .receiptImageUrl(request.getReceiptImageUrl())
                .status("PENDING_VERIFICATION")
                .build();

        payment = paymentRepository.save(payment);
        return mapToResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse approvePayment(UUID paymentId, UUID adminUserId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

        if ("APPROVED".equalsIgnoreCase(payment.getStatus())) {
            throw new BadRequestException("Payment is already approved");
        }

        payment.setStatus("APPROVED");
        payment.setVerifiedBy(adminUserId);
        payment.setVerifiedAt(OffsetDateTime.now());
        payment = paymentRepository.save(payment);

        Subscription subscription = subscriptionRepository.findById(payment.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription record not found"));

        Plan plan = subscription.getPlan();
        int addDays = "ANNUAL".equalsIgnoreCase(plan.getPlanType()) ? 365 : 30;

        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = (subscription.getEndDate() != null && subscription.getEndDate().isAfter(newStartDate))
                ? subscription.getEndDate().plusDays(addDays)
                : newStartDate.plusDays(addDays);

        subscription.setStatus("ACTIVE");
        subscription.setStartDate(newStartDate);
        subscription.setEndDate(newEndDate);
        subscriptionRepository.save(subscription);

        String invoiceNum = "INV-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Invoice invoice = Invoice.builder()
                .organizationId(payment.getOrganizationId())
                .subscriptionId(subscription.getId())
                .paymentId(payment.getId())
                .invoiceNumber(invoiceNum)
                .amount(payment.getAmount())
                .issuedAt(OffsetDateTime.now())
                .build();
        invoiceRepository.save(invoice);

        eventPublisher.publishEvent(new PaymentApprovedEvent(this, payment.getId(), payment.getOrganizationId(), subscription.getId(), payment.getAmount()));

        return mapToResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse rejectPayment(UUID paymentId, String reason, UUID adminUserId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

        payment.setStatus("REJECTED");
        payment.setRejectionReason(reason);
        payment.setVerifiedBy(adminUserId);
        payment.setVerifiedAt(OffsetDateTime.now());
        payment = paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPendingPayments() {
        return paymentRepository.findByStatus("PENDING_VERIFICATION")
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getFarmPaymentHistory(UUID tenantId) {
        return paymentRepository.findByOrganizationId(tenantId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .organizationId(payment.getOrganizationId())
                .subscriptionId(payment.getSubscriptionId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionRef(payment.getTransactionRef())
                .receiptImageUrl(payment.getReceiptImageUrl())
                .status(payment.getStatus())
                .rejectionReason(payment.getRejectionReason())
                .verifiedBy(payment.getVerifiedBy())
                .verifiedAt(payment.getVerifiedAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse getFarmSubscription(UUID tenantId) {
        Subscription sub = subscriptionRepository.findByOrganizationId(tenantId)
                .orElse(null);

        if (sub == null) {
            Plan freePlan = planRepository.findAll().stream()
                    .filter(p -> "FREE".equalsIgnoreCase(p.getPlanType()))
                    .findFirst()
                    .orElse(null);

            return com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse.builder()
                    .organizationId(tenantId)
                    .plan(freePlan)
                    .status("UNCLAIMED")
                    .active(false)
                    .daysRemaining(14)
                    .build();
        }

        boolean isActive = "ACTIVE".equalsIgnoreCase(sub.getStatus()) ||
                ("TRIAL".equalsIgnoreCase(sub.getStatus()) && (sub.getEndDate() == null || !sub.getEndDate().isBefore(LocalDate.now())));

        long daysRemaining = 0;
        if (sub.getEndDate() != null && !sub.getEndDate().isBefore(LocalDate.now())) {
            daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), sub.getEndDate());
        }

        return com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse.builder()
                .id(sub.getId())
                .organizationId(sub.getOrganizationId())
                .plan(sub.getPlan())
                .status(sub.getStatus())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .autoRenew(sub.getAutoRenew())
                .active(isActive)
                .daysRemaining(daysRemaining)
                .build();
    }

    @Override
    @Transactional
    public com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse claimFreeTrial(UUID tenantId) {
        Subscription sub = subscriptionRepository.findByOrganizationId(tenantId)
                .orElse(null);

        if (sub != null && !"UNCLAIMED".equalsIgnoreCase(sub.getStatus())) {
            throw new BadRequestException("Free trial has already been claimed or processed for your farm.");
        }

        Plan freePlan = planRepository.findAll().stream()
                .filter(p -> "FREE".equalsIgnoreCase(p.getPlanType()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Free trial plan is not configured in database"));

        if (sub == null) {
            sub = Subscription.builder()
                    .organizationId(tenantId)
                    .plan(freePlan)
                    .status("TRIAL")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(14))
                    .autoRenew(false)
                    .build();
        } else {
            sub.setPlan(freePlan);
            sub.setStatus("TRIAL");
            sub.setStartDate(LocalDate.now());
            sub.setEndDate(LocalDate.now().plusDays(14));
        }
        subscriptionRepository.save(sub);

        return getFarmSubscription(tenantId);
    }
}
