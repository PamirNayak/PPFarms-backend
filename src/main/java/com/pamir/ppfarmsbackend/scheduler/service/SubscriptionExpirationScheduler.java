package com.pamir.ppfarmsbackend.scheduler.service;

import com.pamir.ppfarmsbackend.billing.entity.Subscription;
import com.pamir.ppfarmsbackend.billing.repository.SubscriptionRepository;
import com.pamir.ppfarmsbackend.identity.entity.Notification;
import com.pamir.ppfarmsbackend.identity.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(SubscriptionExpirationScheduler.class);

    private final SubscriptionRepository subscriptionRepository;
    private final NotificationRepository notificationRepository;

    /**
     * Daily Cron at 01:00 AM: Checks active farm subscriptions for expiration & grace periods.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void scanSubscriptionExpirations() {
        log.info("[CRON] Running Daily Farm Subscription Expiration & Grace Period Scan...");
        List<Subscription> activeSubscriptions = subscriptionRepository.findAll();

        LocalDate now = LocalDate.now();
        int expiredCount = 0;
        int warningCount = 0;

        for (Subscription sub : activeSubscriptions) {
            if (sub.getEndDate() != null) {
                if (sub.getEndDate().isBefore(now.minusDays(3)) && !"EXPIRED".equalsIgnoreCase(sub.getStatus())) {
                    sub.setStatus("EXPIRED");
                    subscriptionRepository.save(sub);
                    expiredCount++;

                    notificationRepository.save(Notification.builder()
                            .organizationId(sub.getOrganizationId())
                            .type("BILLING_EXPIRED")
                            .title("Subscription Expired (Read-Only Mode)")
                            .message("Your farm subscription expired and grace period has ended. Account is now in Read-Only mode. Please renew to restore full access.")
                            .build());

                    log.warn("SUBSCRIPTION EXPIRED: Organization ID [{}] subscription status set to EXPIRED", sub.getOrganizationId());
                } else if (sub.getEndDate().isBefore(now.plusDays(7)) && sub.getEndDate().isAfter(now)) {
                    warningCount++;
                    notificationRepository.save(Notification.builder()
                            .organizationId(sub.getOrganizationId())
                            .type("BILLING_RENEWAL_WARNING")
                            .title("Subscription Expiring Soon")
                            .message("Your farm subscription will expire on " + sub.getEndDate() + ". Submit payment proof now to ensure uninterrupted service.")
                            .build());
                }
            }
        }


        log.info("[CRON] Subscription Scan Finished. Newly Expired: {}, Renewal Warnings: {}", expiredCount, warningCount);
    }
}
