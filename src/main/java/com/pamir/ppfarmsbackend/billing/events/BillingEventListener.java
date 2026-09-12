package com.pamir.ppfarmsbackend.billing.events;

import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.identity.repository.OrganizationRepository;
import com.pamir.ppfarmsbackend.identity.repository.UserRepository;
import com.pamir.ppfarmsbackend.shared.email.service.EmailService;
import com.pamir.ppfarmsbackend.shared.realtime.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BillingEventListener {

    private static final Logger log = LoggerFactory.getLogger(BillingEventListener.class);

    private final EmailService emailService;
    private final SseEmitterService sseEmitterService;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    @Value("${superadmin.email:superadmin@ppfarms.com}")
    private String superAdminEmail;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    private record FarmContact(String farmName, String ownerName, String email) {}

    private FarmContact resolveFarmContact(UUID organizationId) {
        String farmName = "Your Farm";
        String ownerName = "Farm Administrator";
        String email = null;

        if (organizationId != null) {
            var orgOpt = organizationRepository.findById(organizationId);
            if (orgOpt.isPresent()) {
                var org = orgOpt.get();
                farmName = org.getName();
                email = org.getEmail();
            }

            try {
                List<User> users = userRepository.findByOrganizationIdAndDeletedAtIsNull(organizationId);
                if (users != null && !users.isEmpty()) {
                    User owner = users.stream()
                            .filter(u -> u.getRole() != null && ("FARM_ADMIN".equalsIgnoreCase(u.getRole().getName()) || "ADMIN".equalsIgnoreCase(u.getRole().getName()) || "SUPER_ADMIN".equalsIgnoreCase(u.getRole().getName())))
                            .findFirst()
                            .orElse(users.get(0));
                    ownerName = owner.getName();
                    if (email == null || email.isBlank()) {
                        email = owner.getEmail();
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return new FarmContact(farmName, ownerName, email);
    }

    @Async
    @EventListener
    public void handlePaymentSubmitted(PaymentSubmittedEvent event) {
        log.info("[EVENT] Handling PaymentSubmittedEvent for org {}", event.getOrganizationId());
        FarmContact contact = resolveFarmContact(event.getOrganizationId());

        // 1. Real-Time Push to Connected Clients (Instant 0ms UI update)
        try {
            Map<String, Object> ssePayload = Map.of(
                    "type", "PAYMENT_SUBMITTED",
                    "organizationId", event.getOrganizationId().toString(),
                    "farmName", contact.farmName(),
                    "amount", event.getAmount(),
                    "transactionRef", event.getTransactionRef() != null ? event.getTransactionRef() : ""
            );
            sseEmitterService.sendToSuperAdmins("PAYMENT_SUBMITTED", ssePayload);
            sseEmitterService.sendToOrganization(event.getOrganizationId(), "PAYMENT_SUBMITTED", ssePayload);
        } catch (Exception e) {
            log.debug("[SSE] Push error on payment submitted: {}", e.getMessage());
        }

        // 2. Email Super Admin
        try {
            Map<String, Object> adminModel = new HashMap<>();
            adminModel.put("farmName", contact.farmName());
            adminModel.put("ownerName", contact.ownerName());
            adminModel.put("ownerEmail", contact.email() != null ? contact.email() : "N/A");
            adminModel.put("planName", event.getPlanName() != null ? event.getPlanName() : "SaaS Plan");
            adminModel.put("amount", event.getAmount());
            adminModel.put("paymentMethod", event.getPaymentMethod() != null ? event.getPaymentMethod() : "UPI_QR");
            adminModel.put("transactionRef", event.getTransactionRef() != null ? event.getTransactionRef() : "N/A");
            adminModel.put("consoleUrl", frontendUrl + "/super-admin/payments");

            if (superAdminEmail != null && !superAdminEmail.isBlank()) {
                emailService.sendHtmlEmail(
                        superAdminEmail,
                        "Action Required: New Payment Proof Submitted - " + contact.farmName(),
                        "payment-submitted-admin",
                        adminModel
                );
            }
        } catch (Exception e) {
            log.warn("[MAIL] Failed to dispatch admin payment alert: {}", e.getMessage());
        }

        // 3. Email Farm Admin Confirmation
        if (contact.email() != null && !contact.email().isBlank()) {
            try {
                Map<String, Object> farmModel = new HashMap<>();
                farmModel.put("farmName", contact.farmName());
                farmModel.put("ownerName", contact.ownerName());
                farmModel.put("planName", event.getPlanName() != null ? event.getPlanName() : "SaaS Plan");
                farmModel.put("amount", event.getAmount());
                farmModel.put("transactionRef", event.getTransactionRef() != null ? event.getTransactionRef() : "N/A");

                emailService.sendHtmlEmail(
                        contact.email(),
                        "Payment Received & Under Verification - " + contact.farmName(),
                        "payment-submitted-farm",
                        farmModel
                );
            } catch (Exception e) {
                log.warn("[MAIL] Failed to dispatch farm payment acknowledgment: {}", e.getMessage());
            }
        }
    }

    @Async
    @EventListener
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        log.info("[EVENT] Handling PaymentApprovedEvent for org {}", event.getOrganizationId());
        FarmContact contact = resolveFarmContact(event.getOrganizationId());

        // 1. Real-Time Push to Connected Clients (Instant 0ms UI update)
        try {
            Map<String, Object> ssePayload = Map.of(
                    "type", "PAYMENT_APPROVED",
                    "organizationId", event.getOrganizationId().toString(),
                    "planName", event.getPlanName() != null ? event.getPlanName() : "Active Tier",
                    "invoiceNumber", event.getInvoiceNumber() != null ? event.getInvoiceNumber() : "",
                    "status", "ACTIVE"
            );
            sseEmitterService.sendToOrganization(event.getOrganizationId(), "PAYMENT_APPROVED", ssePayload);
            sseEmitterService.sendToSuperAdmins("PAYMENT_APPROVED", ssePayload);
        } catch (Exception e) {
            log.debug("[SSE] Push error on payment approved: {}", e.getMessage());
        }

        // 2. Email Farm Admin
        if (contact.email() != null && !contact.email().isBlank()) {
            try {
                Map<String, Object> model = new HashMap<>();
                model.put("farmName", contact.farmName());
                model.put("ownerName", contact.ownerName());
                model.put("planName", event.getPlanName() != null ? event.getPlanName() : "Commercial Plan");
                model.put("planType", event.getPlanType() != null ? event.getPlanType() : "MONTHLY");
                model.put("amount", event.getAmount());
                model.put("invoiceNumber", event.getInvoiceNumber() != null ? event.getInvoiceNumber() : "N/A");
                model.put("endDate", event.getEndDate() != null ? event.getEndDate().toString() : "Active");
                model.put("transactionRef", event.getTransactionRef() != null ? event.getTransactionRef() : "N/A");
                model.put("dashboardUrl", frontendUrl + "/dashboard");

                emailService.sendHtmlEmail(
                        contact.email(),
                        "Payment Verified: Your Subscription is Active! - " + contact.farmName(),
                        "payment-approved",
                        model
                );
            } catch (Exception e) {
                log.warn("[MAIL] Failed to dispatch payment approval email: {}", e.getMessage());
            }
        }
    }

    @Async
    @EventListener
    public void handlePaymentRejected(PaymentRejectedEvent event) {
        log.info("[EVENT] Handling PaymentRejectedEvent for org {}", event.getOrganizationId());
        FarmContact contact = resolveFarmContact(event.getOrganizationId());

        // 1. Real-Time Push to Connected Clients (Instant 0ms UI update)
        try {
            Map<String, Object> ssePayload = Map.of(
                    "type", "PAYMENT_REJECTED",
                    "organizationId", event.getOrganizationId().toString(),
                    "reason", event.getReason() != null ? event.getReason() : "",
                    "status", "REJECTED"
            );
            sseEmitterService.sendToOrganization(event.getOrganizationId(), "PAYMENT_REJECTED", ssePayload);
            sseEmitterService.sendToSuperAdmins("PAYMENT_REJECTED", ssePayload);
        } catch (Exception e) {
            log.debug("[SSE] Push error on payment rejected: {}", e.getMessage());
        }

        // 2. Email Farm Admin
        if (contact.email() != null && !contact.email().isBlank()) {
            try {
                Map<String, Object> model = new HashMap<>();
                model.put("farmName", contact.farmName());
                model.put("ownerName", contact.ownerName());
                model.put("planName", event.getPlanName() != null ? event.getPlanName() : "SaaS Plan");
                model.put("amount", event.getAmount());
                model.put("reason", event.getReason() != null ? event.getReason() : "Payment reference could not be validated.");
                model.put("transactionRef", event.getTransactionRef() != null ? event.getTransactionRef() : "N/A");
                model.put("billingUrl", frontendUrl + "/settings?tab=billing");

                emailService.sendHtmlEmail(
                        contact.email(),
                        "Action Required: Payment Verification Update - " + contact.farmName(),
                        "payment-rejected",
                        model
                );
            } catch (Exception e) {
                log.warn("[MAIL] Failed to dispatch payment rejection email: {}", e.getMessage());
            }
        }
    }
}