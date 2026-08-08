package com.pamir.ppfarmsbackend.identity.event;

/**
 * Java 21 Sealed Domain Event hierarchy for Authentication domain events.
 */
public sealed interface AuthEvents permits AuthEvents.OtpRequestedEvent, AuthEvents.PasswordResetSuccessEvent {

    record OtpRequestedEvent(
            String email,
            String rawOtp,
            String templateName
    ) implements AuthEvents {}

    record PasswordResetSuccessEvent(
            String email,
            String ownerName
    ) implements AuthEvents {}
}
