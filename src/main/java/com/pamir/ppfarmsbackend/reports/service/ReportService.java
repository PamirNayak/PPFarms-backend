package com.pamir.ppfarmsbackend.reports.service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public interface ReportService {

    ByteArrayOutputStream generateHerdPdfReport(UUID organizationId);

    ByteArrayOutputStream generateHerdExcelReport(UUID organizationId);

    ByteArrayOutputStream generateFinancialPdfReport(UUID organizationId);

    ByteArrayOutputStream generateFinancialExcelReport(UUID organizationId);
}
