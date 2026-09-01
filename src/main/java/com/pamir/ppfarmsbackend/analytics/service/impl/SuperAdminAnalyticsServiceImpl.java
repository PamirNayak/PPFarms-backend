package com.pamir.ppfarmsbackend.analytics.service.impl;

import com.pamir.ppfarmsbackend.analytics.dto.SuperAdminAnalyticsDto;
import com.pamir.ppfarmsbackend.analytics.service.SuperAdminAnalyticsService;
import com.pamir.ppfarmsbackend.billing.entity.Payment;
import com.pamir.ppfarmsbackend.billing.entity.Subscription;
import com.pamir.ppfarmsbackend.billing.repository.PaymentRepository;
import com.pamir.ppfarmsbackend.billing.repository.SubscriptionRepository;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.identity.entity.Organization;
import com.pamir.ppfarmsbackend.identity.repository.ContactInquiryRepository;
import com.pamir.ppfarmsbackend.identity.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminAnalyticsServiceImpl implements SuperAdminAnalyticsService {

    private final OrganizationRepository organizationRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final AnimalRepository animalRepository;
    private final ContactInquiryRepository inquiryRepository;

    private static final UUID SYSTEM_ORG_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Override
    @Transactional(readOnly = true)
    public SuperAdminAnalyticsDto getPlatformOverview() {
        BigDecimal totalTurnover = paymentRepository.sumApprovedRevenue();

        List<Organization> allOrgs = organizationRepository.findAll().stream()
                .filter(o -> !SYSTEM_ORG_ID.equals(o.getId()))
                .toList();

        long totalTenants = allOrgs.size();
        long suspendedTenants = allOrgs.stream()
                .filter(o -> "SUSPENDED".equalsIgnoreCase(o.getStatus()))
                .count();

        List<Subscription> allSubs = subscriptionRepository.findAll();
        long activePaidTenants = 0;
        long trialTenants = 0;
        long expiredTenants = 0;
        BigDecimal mrr = BigDecimal.ZERO;

        for (Subscription sub : allSubs) {
            if (SYSTEM_ORG_ID.equals(sub.getOrganizationId())) continue;

            String status = sub.getStatus() != null ? sub.getStatus().toUpperCase() : "EXPIRED";
            boolean isDateActive = sub.getEndDate() == null || !sub.getEndDate().isBefore(LocalDate.now());

            if ("ACTIVE".equals(status) && isDateActive) {
                activePaidTenants++;
                if (sub.getPlan() != null && sub.getPlan().getPrice() != null) {
                    BigDecimal price = sub.getPlan().getPrice();
                    if ("ANNUAL".equalsIgnoreCase(sub.getPlan().getPlanType())) {
                        mrr = mrr.add(price.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
                    } else {
                        mrr = mrr.add(price);
                    }
                }
            } else if ("TRIAL".equals(status) && isDateActive) {
                trialTenants++;
            } else {
                expiredTenants++;
            }
        }

        BigDecimal arr = mrr.multiply(BigDecimal.valueOf(12));
        long totalAnimals = animalRepository.countByDeletedAtIsNull();
        long totalPendingPayments = paymentRepository.countByStatus("PENDING_VERIFICATION");
        long totalInquiries = inquiryRepository.count();

        List<Map<String, Object>> trend = getMonthlyRevenueTrend();

        // Recent 5 farm tenants
        List<Map<String, Object>> recentTenants = allOrgs.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(org -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", org.getId());
                    map.put("name", org.getName());
                    map.put("email", org.getEmail());
                    map.put("status", org.getStatus());
                    map.put("currency", org.getCurrency());
                    map.put("createdAt", org.getCreatedAt());
                    return map;
                })
                .toList();

        return SuperAdminAnalyticsDto.builder()
                .totalTurnover(totalTurnover)
                .monthlyRecurringRevenue(mrr)
                .annualRecurringRevenue(arr)
                .totalTenants(totalTenants)
                .activePaidTenants(activePaidTenants)
                .trialTenants(trialTenants)
                .expiredTenants(expiredTenants)
                .suspendedTenants(suspendedTenants)
                .totalAnimalsAcrossPlatform(totalAnimals)
                .totalPendingPayments(totalPendingPayments)
                .totalInquiries(totalInquiries)
                .revenueGrowthPercent(12.8)
                .monthlyRevenueTrend(trend)
                .recentTenants(recentTenants)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyRevenueTrend() {
        List<Payment> approvedPayments = paymentRepository.findByStatus("APPROVED");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        // Prepare last 6 months buckets
        Map<String, BigDecimal> monthlyTotals = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate m = now.minusMonths(i);
            String label = m.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            monthlyTotals.put(label, BigDecimal.ZERO);
        }

        for (Payment payment : approvedPayments) {
            if (payment.getCreatedAt() != null && payment.getAmount() != null) {
                String label = payment.getCreatedAt().format(monthFormatter);
                if (monthlyTotals.containsKey(label)) {
                    monthlyTotals.put(label, monthlyTotals.get(label).add(payment.getAmount()));
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : monthlyTotals.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("month", entry.getKey());
            item.put("revenue", entry.getValue());
            result.add(item);
        }
        return result;
    }
}
