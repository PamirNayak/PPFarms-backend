package com.pamir.ppfarmsbackend.reports.dto;

import java.time.LocalDate;

public class ReportExportRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private String format; // PDF, EXCEL

    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getFormat() { return format; } public void setFormat(String format) { this.format = format; }
}
