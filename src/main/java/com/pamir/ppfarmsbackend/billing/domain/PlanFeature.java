package com.pamir.ppfarmsbackend.billing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum PlanFeature {

    MILK_LOGGING("milkYieldTracking", "Daily Milk Production & Yield Logger", "Milk", "Daily morning & evening milking sessions, yield graphs, fat % logs"),
    PEDIGREE("pedigreeTree", "3-Generation Pedigree & Lineage Tree", "Dna", "Sire/dam family tree, inbreeding coefficient radar"),
    VET_MODULE("vetModule", "Veterinary & Health Radar", "Stethoscope", "Disease treatments, vaccination schedules, quarantine records"),
    ACCOUNTING("accounting", "Financials & Accounting Ledger", "Receipt", "Income/expense tracking, invoices, P&L reporting"),
    REPORTS("reportsExport", "PDF & Excel Audit Reports", "BarChart3", "Downloadable PDF/Excel farm performance & tax audits"),
    EXCEL_EXPORT("excelExport", "Bulk Excel Data Export", "FileSpreadsheet", "Export herd, milk and accounting records to Microsoft Excel"),
    MULTI_SPECIES("multiSpecies", "Multi-Species Farm Engine", "Box", "Manage Cattle, Goats, Sheep, and Poultry simultaneously"),
    MULTI_SHED("multiShedSupport", "Multi-Shed & Pen Housing Management", "Layers", "Organize animals across barns, sheds, and isolation pens"),
    FCR_ANALYTICS("fcrAnalytics", "FCR Feed Conversion Analytics", "Wheat", "Track feed conversion efficiency, rations, and stock usage"),
    FEED_AUTO_DEDUCTION("feedAutoDeduction", "Automated Feed Stock Deduction", "Package", "Automatic stock deduction upon feeding session logging"),
    AUDIT_LOGS("auditLogs", "Security & Compliance Audit Logs", "ShieldCheck", "Track all employee activities, modifications, and access history"),
    CUSTOM_REPORTS("customReports", "Custom Reports Builder", "FileText", "Create bespoke operational, production and financial reports"),
    BASIC_LOGS("basicLogs", "Basic Operational Logs", "ClipboardList", "Daily animal logging, ear tagging, and observations");

    private final String key;
    private final String displayName;
    private final String icon;
    private final String description;

    public static Optional<PlanFeature> fromKey(String key) {
        if (key == null) return Optional.empty();
        return Arrays.stream(values())
                .filter(f -> f.getKey().equalsIgnoreCase(key) ||
                             f.name().equalsIgnoreCase(key) ||
                             f.getKey().replace("_", "").equalsIgnoreCase(key.replace("_", "")))
                .findFirst();
    }

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
