package com.pamir.ppfarmsbackend.analytics.service.impl;

import com.pamir.ppfarmsbackend.accounting.dto.FinancialSummaryResponse;
import com.pamir.ppfarmsbackend.accounting.service.FinancialService;
import com.pamir.ppfarmsbackend.analytics.dto.*;
import com.pamir.ppfarmsbackend.analytics.service.AnalyticsService;
import com.pamir.ppfarmsbackend.herd.domain.AnimalGender;
import com.pamir.ppfarmsbackend.herd.domain.AnimalStatus;
import com.pamir.ppfarmsbackend.herd.entity.Animal;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.production.entity.ProductionRecord;
import com.pamir.ppfarmsbackend.production.repository.ProductionRecordRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final AnimalRepository animalRepository;
    private final ProductionRecordRepository productionRecordRepository;
    private final FinancialService financialService;

    public AnalyticsServiceImpl(AnimalRepository animalRepository,
                                ProductionRecordRepository productionRecordRepository,
                                FinancialService financialService) {
        this.animalRepository = animalRepository;
        this.productionRecordRepository = productionRecordRepository;
        this.financialService = financialService;
    }

    @Override
    public DashboardAnalyticsResponse getDashboardAnalytics(UUID organizationId) {
        return getDashboardAnalytics(organizationId, null);
    }

    @Override
    @Cacheable(value = "dashboard_analytics", key = "#organizationId.toString() + '_' + (#speciesId != null ? #speciesId.toString() : 'ALL')")
    public DashboardAnalyticsResponse getDashboardAnalytics(UUID organizationId, UUID speciesId) {
        HerdAnalyticsDto herd = buildHerdAnalytics(organizationId, speciesId);
        ProductionAnalyticsDto production = buildProductionAnalytics(organizationId, speciesId);
        FinancialAnalyticsDto financial = buildFinancialAnalytics(organizationId);

        return DashboardAnalyticsResponse.builder()
                .organizationId(organizationId)
                .herd(herd)
                .production(production)
                .financial(financial)
                .build();
    }

    private HerdAnalyticsDto buildHerdAnalytics(UUID organizationId, UUID speciesId) {
        Specification<Animal> spec = (root, query, cb) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
            predicates.add(cb.equal(root.get("organizationId"), organizationId));
            if (speciesId != null) {
                predicates.add(cb.equal(root.get("species").get("id"), speciesId));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        List<Animal> animals = animalRepository.findAll(spec);

        long total = animals.size();
        long active = animals.stream().filter(a -> a.getStatus() == AnimalStatus.ACTIVE).count();
        long sold = animals.stream().filter(a -> a.getStatus() == AnimalStatus.SOLD).count();
        long deceased = animals.stream().filter(a -> a.getStatus() == AnimalStatus.DECEASED || a.getStatus() == AnimalStatus.CULLED).count();

        double mortalityRate = total > 0 ? ((double) deceased / total) * 100.0 : 0.0;

        Map<String, Long> speciesMap = animals.stream()
                .filter(a -> a.getSpecies() != null)
                .collect(Collectors.groupingBy(a -> a.getSpecies().getName(), Collectors.counting()));

        Map<String, Long> genderMap = animals.stream()
                .filter(a -> a.getGender() != null)
                .collect(Collectors.groupingBy(a -> a.getGender().name(), Collectors.counting()));

        return HerdAnalyticsDto.builder()
                .totalAnimals(total)
                .activeAnimals(active)
                .soldAnimals(sold)
                .deceasedAnimals(deceased)
                .mortalityRatePercentage(Math.round(mortalityRate * 100.0) / 100.0)
                .animalsBySpecies(speciesMap)
                .animalsByGender(genderMap)
                .build();
    }

    private ProductionAnalyticsDto buildProductionAnalytics(UUID organizationId, UUID speciesId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        List<ProductionRecord> records = productionRecordRepository.findByOrganizationIdAndRecordedDateBetween(
                organizationId, startOfMonth, LocalDate.now());

        if (speciesId != null) {
            Set<UUID> animalIdsOfSpecies = animalRepository.findAll((root, query, cb) ->
                    cb.and(
                            cb.equal(root.get("organizationId"), organizationId),
                            cb.equal(root.get("species").get("id"), speciesId)
                    )
            ).stream().map(Animal::getId).collect(Collectors.toSet());

            records = records.stream()
                    .filter(r -> r.getAnimalId() != null && animalIdsOfSpecies.contains(r.getAnimalId()))
                    .toList();
        }

        BigDecimal totalMilk = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        BigDecimal totalSnf = BigDecimal.ZERO;
        int countWithFat = 0;
        int countWithSnf = 0;

        Map<String, BigDecimal> typeMap = new HashMap<>();

        for (ProductionRecord r : records) {
            BigDecimal qty = r.getQuantity() != null ? r.getQuantity() : BigDecimal.ZERO;
            totalMilk = totalMilk.add(qty);

            String pType = r.getProductionType() != null ? r.getProductionType() : "MILK";
            typeMap.put(pType, typeMap.getOrDefault(pType, BigDecimal.ZERO).add(qty));

            if (r.getFatPercentage() != null) {
                totalFat = totalFat.add(r.getFatPercentage());
                countWithFat++;
            }
            if (r.getSnfPercentage() != null) {
                totalSnf = totalSnf.add(r.getSnfPercentage());
                countWithSnf++;
            }
        }

        int dayOfMonth = LocalDate.now().getDayOfMonth();
        BigDecimal avgDaily = dayOfMonth > 0 ? totalMilk.divide(BigDecimal.valueOf(dayOfMonth), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgFat = countWithFat > 0 ? totalFat.divide(BigDecimal.valueOf(countWithFat), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal avgSnf = countWithSnf > 0 ? totalSnf.divide(BigDecimal.valueOf(countWithSnf), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        return ProductionAnalyticsDto.builder()
                .totalMilkThisMonth(totalMilk)
                .averageDailyMilkYield(avgDaily)
                .averageFatPercentage(avgFat)
                .averageSnfPercentage(avgSnf)
                .productionByType(typeMap)
                .build();
    }

    private FinancialAnalyticsDto buildFinancialAnalytics(UUID organizationId) {
        FinancialSummaryResponse summary = financialService.getFinancialSummary(organizationId);

        BigDecimal revenue = summary.getTotalIncome() != null ? summary.getTotalIncome() : BigDecimal.ZERO;
        BigDecimal expenses = summary.getTotalExpense() != null ? summary.getTotalExpense() : BigDecimal.ZERO;
        BigDecimal netProfit = summary.getNetProfitLoss() != null ? summary.getNetProfitLoss() : BigDecimal.ZERO;

        double profitMargin = 0.0;
        if (revenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMargin = netProfit.divide(revenue, 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
        }

        return FinancialAnalyticsDto.builder()
                .totalRevenue(revenue)
                .totalExpenses(expenses)
                .netProfitLoss(netProfit)
                .profitMarginPercentage(Math.round(profitMargin * 100.0) / 100.0)
                .topExpenseCategories(summary.getExpenseByCategory())
                .topIncomeCategories(summary.getIncomeByCategory())
                .build();
    }

    @Override
    public List<Map<String, Object>> getMilkProductionTrend(UUID organizationId, int days) {
        return getMilkProductionTrend(organizationId, days, null);
    }

    @Override
    public List<Map<String, Object>> getMilkProductionTrend(UUID organizationId, int days, UUID speciesId) {
        LocalDate startDate = LocalDate.now().minusDays(days > 0 ? days : 30);
        List<ProductionRecord> records = productionRecordRepository.findByOrganizationIdAndRecordedDateBetween(
                organizationId, startDate, LocalDate.now());

        if (speciesId != null) {
            Set<UUID> animalIdsOfSpecies = animalRepository.findAll((root, query, cb) ->
                    cb.and(
                            cb.equal(root.get("organizationId"), organizationId),
                            cb.equal(root.get("species").get("id"), speciesId)
                    )
            ).stream().map(Animal::getId).collect(Collectors.toSet());

            records = records.stream()
                    .filter(r -> r.getAnimalId() != null && animalIdsOfSpecies.contains(r.getAnimalId()))
                    .toList();
        }

        Map<LocalDate, BigDecimal> dailyMap = new TreeMap<>();
        for (ProductionRecord r : records) {
            dailyMap.put(r.getRecordedDate(), dailyMap.getOrDefault(r.getRecordedDate(), BigDecimal.ZERO).add(
                    r.getQuantity() != null ? r.getQuantity() : BigDecimal.ZERO));
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> entry : dailyMap.entrySet()) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", entry.getKey().toString());
            point.put("milkLiters", entry.getValue());
            result.add(point);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getFinancialTrend(UUID organizationId, int months) {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        int mCount = months > 0 ? months : 6;

        for (int i = mCount - 1; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            String monthName = monthDate.getMonth().toString().substring(0, 3) + " " + monthDate.getYear();

            FinancialSummaryResponse summary = financialService.getFinancialSummary(organizationId);

            Map<String, Object> point = new HashMap<>();
            point.put("month", monthName);
            point.put("income", summary.getTotalIncome() != null ? summary.getTotalIncome() : BigDecimal.ZERO);
            point.put("expense", summary.getTotalExpense() != null ? summary.getTotalExpense() : BigDecimal.ZERO);
            point.put("profit", summary.getNetProfitLoss() != null ? summary.getNetProfitLoss() : BigDecimal.ZERO);
            result.add(point);
        }
        return result;
    }
}

