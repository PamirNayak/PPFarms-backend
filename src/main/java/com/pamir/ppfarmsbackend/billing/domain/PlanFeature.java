package com.pamir.ppfarmsbackend.billing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum PlanFeature {

    MILK_LOGGING("milkLogging", "Daily Milk Production", "Milk", "Daily morning & evening milking sessions, yield graphs, fat % logs"),
    PEDIGREE("pedigree", "Pedigree & 3-Gen Lineage", "Dna", "Sire/dam family tree, inbreeding coefficient radar"),
    VET_MODULE("vetModule", "Veterinary & Health Radar", "Stethoscope", "Disease treatments, vaccination schedules, quarantine records"),
    ACCOUNTING("accounting", "Financials & Accounting Ledger", "Receipt", "Income/expense tracking, invoices, P&L reporting"),
    REPORTS("reports", "Executive Analytics & PDF Reports", "BarChart3", "Downloadable PDF/Excel farm performance & tax audits");

    private final String key;
    private final String displayName;
    private final String icon;
    private final String description;

    public static List<Map<String, String>> toCatalogList() {
        return Arrays.stream(values())
                .map(f -> Map.of(
                        "key", f.getKey(),
                        "name", f.getDisplayName(),
                        "icon", f.getIcon(),
                        "description", f.getDescription()
                ))
                .toList();
    }
}
