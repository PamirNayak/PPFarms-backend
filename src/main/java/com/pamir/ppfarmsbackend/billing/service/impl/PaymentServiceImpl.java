package com.pamir.ppfarmsbackend.billing.service.impl;

import com.pamir.ppfarmsbackend.billing.dto.PaymentResponse;
import com.pamir.ppfarmsbackend.billing.dto.PaymentSubmitRequest;
import com.pamir.ppfarmsbackend.billing.entity.Invoice;
import com.pamir.ppfarmsbackend.billing.entity.Payment;
import com.pamir.ppfarmsbackend.billing.entity.Plan;
import com.pamir.ppfarmsbackend.billing.entity.Subscription;
import com.pamir.ppfarmsbackend.billing.events.PaymentApprovedEvent;
import com.pamir.ppfarmsbackend.billing.events.PaymentRejectedEvent;
import com.pamir.ppfarmsbackend.billing.events.PaymentSubmittedEvent;
import com.pamir.ppfarmsbackend.billing.repository.InvoiceRepository;
import com.pamir.ppfarmsbackend.billing.repository.PaymentRepository;
import com.pamir.ppfarmsbackend.billing.repository.PlanRepository;
import com.pamir.ppfarmsbackend.billing.repository.SubscriptionRepository;
import com.pamir.ppfarmsbackend.billing.service.PaymentService;
import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.identity.repository.OrganizationRepository;
import com.pamir.ppfarmsbackend.identity.repository.UserRepository;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import com.pamir.ppfarmsbackend.shared.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    private void validateTenantExists(UUID tenantId) {
        if (tenantId == null || !organizationRepository.existsById(tenantId)) {
            throw new UnauthorizedException("Farm organization was not found or has been deactivated. Please sign in again.");
        }
    }

    @Override
    @Transactional
    public PaymentResponse submitPaymentProof(PaymentSubmitRequest request, UUID tenantId) {
        validateTenantExists(tenantId);

        Plan targetPlan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        if ("FREE".equalsIgnoreCase(targetPlan.getPlanType()) || (targetPlan.getPrice() != null && targetPlan.getPrice().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new BadRequestException("The Free Trial plan cannot be purchased via payment proof. Please select a valid paid subscription plan.");
        }

        Subscription subscription = subscriptionRepository.findByOrganizationId(tenantId)
                .orElseGet(() -> {
                    Subscription newSub = Subscription.builder()
                            .organizationId(tenantId)
                            .plan(targetPlan)
                            .status("PENDING_APPROVAL")
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now().plusDays(14))
                            .trialUsed(true)
                            .build();
                    return subscriptionRepository.save(newSub);
                });

        Payment payment = Payment.builder()
                .organizationId(tenantId)
                .subscriptionId(subscription.getId())
                .targetPlanId(targetPlan.getId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionRef(request.getTransactionRef())
                .receiptImageUrl(request.getReceiptImageUrl())
                .status("PENDING_VERIFICATION")
                .build();

        payment = paymentRepository.save(payment);

        eventPublisher.publishEvent(new PaymentSubmittedEvent(
                this,
                payment.getId(),
                payment.getOrganizationId(),
                targetPlan.getName(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getTransactionRef()
        ));

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

        // Use targetPlan from payment if available; fallback to current subscription plan
        Plan plan = (payment.getTargetPlanId() != null)
                ? planRepository.findById(payment.getTargetPlanId()).orElse(subscription.getPlan())
                : subscription.getPlan();

        int addDays = "ANNUAL".equalsIgnoreCase(plan.getPlanType()) ? 365 : 30;

        LocalDate newStartDate = LocalDate.now();
        LocalDate newEndDate = (subscription.getEndDate() != null && subscription.getEndDate().isAfter(newStartDate))
                ? subscription.getEndDate().plusDays(addDays)
                : newStartDate.plusDays(addDays);

        subscription.setPlan(plan);
        subscription.setStatus("ACTIVE");
        subscription.setStartDate(newStartDate);
        subscription.setEndDate(newEndDate);
        subscription.setTrialUsed(true);
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

        eventPublisher.publishEvent(new PaymentApprovedEvent(
                this,
                payment.getId(),
                payment.getOrganizationId(),
                subscription.getId(),
                invoiceNum,
                plan.getName(),
                plan.getPlanType(),
                payment.getAmount(),
                newStartDate,
                newEndDate,
                payment.getTransactionRef()
        ));

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

        String planName = "SaaS Plan";
        if (payment.getSubscriptionId() != null) {
            var subOpt = subscriptionRepository.findById(payment.getSubscriptionId());
            if (subOpt.isPresent() && subOpt.get().getPlan() != null) {
                planName = subOpt.get().getPlan().getName();
            }
        }

        eventPublisher.publishEvent(new PaymentRejectedEvent(
                this,
                payment.getId(),
                payment.getOrganizationId(),
                reason,
                payment.getAmount(),
                payment.getTransactionRef(),
                planName
        ));

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
        validateTenantExists(tenantId);
        return paymentRepository.findByOrganizationId(tenantId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        String orgName = null;
        String orgEmail = null;
        String orgPhone = null;
        String ownerName = null;

        if (payment.getOrganizationId() != null) {
            var orgOpt = organizationRepository.findById(payment.getOrganizationId());
            if (orgOpt.isPresent()) {
                var org = orgOpt.get();
                orgName = org.getName();
                orgEmail = org.getEmail();
                orgPhone = org.getPhone();
            }

            try {
                List<User> orgUsers = userRepository.findByOrganizationIdAndDeletedAtIsNull(payment.getOrganizationId());
                if (orgUsers != null && !orgUsers.isEmpty()) {
                    User owner = orgUsers.stream()
                            .filter(u -> u.getRole() != null && ("FARM_ADMIN".equalsIgnoreCase(u.getRole().getName()) || "ADMIN".equalsIgnoreCase(u.getRole().getName()) || "SUPER_ADMIN".equalsIgnoreCase(u.getRole().getName())))
                            .findFirst()
                            .orElse(orgUsers.get(0));
                    ownerName = owner.getName();
                    if (orgEmail == null || orgEmail.isBlank()) {
                        orgEmail = owner.getEmail();
                    }
                    if (orgPhone == null || orgPhone.isBlank()) {
                        orgPhone = owner.getPhone();
                    }
                }
            } catch (Exception ignored) {
            }
        }

        String planName = null;
        String planType = null;
        BigDecimal planPrice = null;
        if (payment.getTargetPlanId() != null) {
            var planOpt = planRepository.findById(payment.getTargetPlanId());
            if (planOpt.isPresent()) {
                var plan = planOpt.get();
                planName = plan.getName();
                planType = plan.getPlanType();
                planPrice = plan.getPrice();
            }
        }
        if (planName == null && payment.getSubscriptionId() != null) {
            var subOpt = subscriptionRepository.findById(payment.getSubscriptionId());
            if (subOpt.isPresent() && subOpt.get().getPlan() != null) {
                var plan = subOpt.get().getPlan();
                planName = plan.getName();
                planType = plan.getPlanType();
                planPrice = plan.getPrice();
            }
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .organizationId(payment.getOrganizationId())
                .organizationName(orgName)
                .farmName(orgName)
                .ownerName(ownerName)
                .organizationEmail(orgEmail)
                .organizationPhone(orgPhone)
                .subscriptionId(payment.getSubscriptionId())
                .targetPlanId(payment.getTargetPlanId())
                .planName(planName)
                .planType(planType)
                .planPrice(planPrice)
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
    @Transactional
    public com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse getFarmSubscription(UUID tenantId) {
        validateTenantExists(tenantId);
        Subscription sub = subscriptionRepository.findByOrganizationId(tenantId)
                .orElse(null);

        if (sub == null) {
            Plan freePlan = planRepository.findAll().stream()
                    .filter(p -> "FREE".equalsIgnoreCase(p.getPlanType()))
                    .findFirst()
                    .orElse(null);

            if (freePlan != null) {
                sub = Subscription.builder()
                        .organizationId(tenantId)
                        .plan(freePlan)
                        .status("TRIAL")
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(14))
                        .autoRenew(false)
                        .trialUsed(true)
                        .build();
                sub = subscriptionRepository.save(sub);
            }
        }

        if (sub == null) {
            return com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse.builder()
                    .organizationId(tenantId)
                    .status("TRIAL")
                    .active(true)
                    .daysRemaining(14)
                    .trialUsed(true)
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
                .trialUsed(sub.getTrialUsed() != null ? sub.getTrialUsed() : true)
                .build();
    }

    @Override
    @Transactional
    public com.pamir.ppfarmsbackend.billing.dto.SubscriptionResponse claimFreeTrial(UUID tenantId) {
        validateTenantExists(tenantId);
        Subscription sub = subscriptionRepository.findByOrganizationId(tenantId)
                .orElse(null);

        if (sub != null && (Boolean.TRUE.equals(sub.getTrialUsed()) || !"UNCLAIMED".equalsIgnoreCase(sub.getStatus()))) {
            throw new BadRequestException("Free trial has already been used for your farm and cannot be claimed again.");
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
                    .trialUsed(true)
                    .build();
        } else {
            sub.setPlan(freePlan);
            sub.setStatus("TRIAL");
            sub.setStartDate(LocalDate.now());
            sub.setEndDate(LocalDate.now().plusDays(14));
            sub.setTrialUsed(true);
        }
        subscriptionRepository.save(sub);

        return getFarmSubscription(tenantId);
    }
}
