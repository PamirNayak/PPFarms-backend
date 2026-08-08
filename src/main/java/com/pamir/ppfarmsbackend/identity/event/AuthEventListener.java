package com.pamir.ppfarmsbackend.identity.event;

import com.pamir.ppfarmsbackend.shared.email.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Decoupled Spring Event Listener handling authentication notifications asynchronously.
 */
@Component
public class AuthEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthEventListener.class);

    private final EmailService emailService;

    public AuthEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void handleOtpRequested(AuthEvents.OtpRequestedEvent event) {
        log.info("[EVENT LISTENED] Dispatching OTP email asynchronously to target: {}", event.email());
        Map<String, Object> model = new HashMap<>();
        model.put("otp", event.rawOtp());
        model.put("email", event.email());

        emailService.sendHtmlEmail(
                event.email(),
                "PP-Farms Account Verification OTP",
                event.templateName() != null ? event.templateName() : "otp-email",
                model
        );
    }

    @Async
    @EventListener
    public void handlePasswordResetSuccess(AuthEvents.PasswordResetSuccessEvent event) {
        log.info("[EVENT LISTENED] Dispatching Password Reset alert to target: {}", event.email());
        Map<String, Object> model = new HashMap<>();
        model.put("name", event.ownerName() != null ? event.ownerName() : "Farm Administrator");

        emailService.sendHtmlEmail(
                event.email(),
                "PP-Farms Security Alert: Password Updated",
                "password-reset-success",
                model
        );
    }
}
