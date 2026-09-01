package com.pamir.ppfarmsbackend.reports.controller;

import com.pamir.ppfarmsbackend.reports.service.ReportService;
import com.pamir.ppfarmsbackend.shared.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reporting Engine (PDF & Excel Exports)", description = "Endpoints for generating downloadable PDF documents and Excel data spreadsheets")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/herd/pdf")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Download Herd Inventory PDF", description = "Generates and downloads styled PDF report of all farm livestock")
    public ResponseEntity<byte[]> downloadHerdPdf(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ByteArrayOutputStream pdfStream = reportService.generateHerdPdfReport(userDetails.getTenantId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=herd_inventory_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.toByteArray());
    }

    @GetMapping("/herd/excel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Download Herd Inventory Excel", description = "Generates and downloads XLSX spreadsheet of farm herd")
    public ResponseEntity<byte[]> downloadHerdExcel(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ByteArrayOutputStream excelStream = reportService.generateHerdExcelReport(userDetails.getTenantId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=herd_inventory_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelStream.toByteArray());
    }

    @GetMapping("/financial/pdf")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Download Financial P&L PDF", description = "Generates and downloads styled PDF financial statement")
    public ResponseEntity<byte[]> downloadFinancialPdf(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ByteArrayOutputStream pdfStream = reportService.generateFinancialPdfReport(userDetails.getTenantId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=financial_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfStream.toByteArray());
    }

    @GetMapping("/financial/excel")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Download Financial P&L Excel", description = "Generates and downloads XLSX spreadsheet of financial ledger")
    public ResponseEntity<byte[]> downloadFinancialExcel(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ByteArrayOutputStream excelStream = reportService.generateFinancialExcelReport(userDetails.getTenantId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=financial_report.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelStream.toByteArray());
    }
}
