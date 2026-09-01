package com.pamir.ppfarmsbackend.analytics.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsResponse {

    private UUID organizationId;
    private HerdAnalyticsDto herd;
    private ProductionAnalyticsDto production;
    private FinancialAnalyticsDto financial;

    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public HerdAnalyticsDto getHerd() { return herd; } public void setHerd(HerdAnalyticsDto herd) { this.herd = herd; }
    public ProductionAnalyticsDto getProduction() { return production; } public void setProduction(ProductionAnalyticsDto production) { this.production = production; }
    public FinancialAnalyticsDto getFinancial() { return financial; } public void setFinancial(FinancialAnalyticsDto financial) { this.financial = financial; }

    public static DashboardAnalyticsResponseBuilder builder() { return new DashboardAnalyticsResponseBuilder(); }
    public static class DashboardAnalyticsResponseBuilder {
        private final DashboardAnalyticsResponse r = new DashboardAnalyticsResponse();
        public DashboardAnalyticsResponseBuilder organizationId(UUID organizationId) { r.setOrganizationId(organizationId); return this; }
        public DashboardAnalyticsResponseBuilder herd(HerdAnalyticsDto herd) { r.setHerd(herd); return this; }
        public DashboardAnalyticsResponseBuilder production(ProductionAnalyticsDto production) { r.setProduction(production); return this; }
        public DashboardAnalyticsResponseBuilder financial(FinancialAnalyticsDto financial) { r.setFinancial(financial); return this; }
        public DashboardAnalyticsResponse build() { return r; }
    }
}
