package com.pamir.ppfarmsbackend.billing.dto;

import com.pamir.ppfarmsbackend.billing.entity.Plan;

import java.time.LocalDate;
import java.util.UUID;

public class SubscriptionResponse {
    private UUID id;
    private UUID organizationId;
    private Plan plan;
    private String status; // UNCLAIMED, TRIAL, ACTIVE, EXPIRED, PENDING_APPROVAL
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean autoRenew;
    private boolean active;
    private long daysRemaining;
    private Boolean trialUsed;

    public SubscriptionResponse() {}

    public SubscriptionResponse(UUID id, UUID organizationId, Plan plan, String status, LocalDate startDate, LocalDate endDate, Boolean autoRenew, boolean active, long daysRemaining, Boolean trialUsed) {
        this.id = id;
        this.organizationId = organizationId;
        this.plan = plan;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.autoRenew = autoRenew;
        this.active = active;
        this.daysRemaining = daysRemaining;
        this.trialUsed = trialUsed;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOrganizationId() { return organizationId; }
    public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Boolean getAutoRenew() { return autoRenew; }
    public void setAutoRenew(Boolean autoRenew) { this.autoRenew = autoRenew; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }
    public Boolean getTrialUsed() { return trialUsed; }
    public void setTrialUsed(Boolean trialUsed) { this.trialUsed = trialUsed; }

    public static SubscriptionResponseBuilder builder() { return new SubscriptionResponseBuilder(); }

    public static class SubscriptionResponseBuilder {
        private UUID id;
        private UUID organizationId;
        private Plan plan;
        private String status;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean autoRenew;
        private boolean active;
        private long daysRemaining;
        private Boolean trialUsed;

        public SubscriptionResponseBuilder id(UUID id) { this.id = id; return this; }
        public SubscriptionResponseBuilder organizationId(UUID organizationId) { this.organizationId = organizationId; return this; }
        public SubscriptionResponseBuilder plan(Plan plan) { this.plan = plan; return this; }
        public SubscriptionResponseBuilder status(String status) { this.status = status; return this; }
        public SubscriptionResponseBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public SubscriptionResponseBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public SubscriptionResponseBuilder autoRenew(Boolean autoRenew) { this.autoRenew = autoRenew; return this; }
        public SubscriptionResponseBuilder active(boolean active) { this.active = active; return this; }
        public SubscriptionResponseBuilder daysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; return this; }
        public SubscriptionResponseBuilder trialUsed(Boolean trialUsed) { this.trialUsed = trialUsed; return this; }

        public SubscriptionResponse build() {
            return new SubscriptionResponse(id, organizationId, plan, status, startDate, endDate, autoRenew, active, daysRemaining, trialUsed);
        }
    }
}
