package com.pamir.ppfarmsbackend.reports.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import com.pamir.ppfarmsbackend.accounting.dto.ExpenseResponse;
import com.pamir.ppfarmsbackend.accounting.dto.IncomeResponse;
import com.pamir.ppfarmsbackend.accounting.service.FinancialService;
import com.pamir.ppfarmsbackend.herd.entity.Animal;
import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.reports.service.ReportService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final AnimalRepository animalRepository;
    private final FinancialService financialService;

    public ReportServiceImpl(AnimalRepository animalRepository, FinancialService financialService) {
        this.animalRepository = animalRepository;
        this.financialService = financialService;
    }

    @Override
    public ByteArrayOutputStream generateHerdPdfReport(UUID organizationId) {
        Specification<Animal> spec = (root, query, cb) -> cb.equal(root.get("organizationId"), organizationId);
        List<Animal> animals = animalRepository.findAll(spec);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph title = new Paragraph("Herd Inventory Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Generated Date: " + java.time.LocalDate.now()));
            document.add(new Paragraph("Total Animals: " + animals.size()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            addPdfTableHeader(table, "Tag Number", "Species", "Breed", "Gender", "Status");

            for (Animal animal : animals) {
                table.addCell(animal.getTagNumber() != null ? animal.getTagNumber() : "-");
                table.addCell(animal.getSpecies() != null ? animal.getSpecies().getName() : "-");
                table.addCell(animal.getBreed() != null ? animal.getBreed().getName() : "-");
                table.addCell(animal.getGender() != null ? animal.getGender().name() : "-");
                table.addCell(animal.getStatus() != null ? animal.getStatus().name() : "-");
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate Herd PDF report: " + e.getMessage());
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateHerdExcelReport(UUID organizationId) {
        Specification<Animal> spec = (root, query, cb) -> cb.equal(root.get("organizationId"), organizationId);
        List<Animal> animals = animalRepository.findAll(spec);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Herd Inventory");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Tag Number", "Name", "Species", "Breed", "Gender", "Status", "DOB", "Birth Weight (kg)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (Animal animal : animals) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(animal.getTagNumber());
                row.createCell(1).setCellValue(animal.getName() != null ? animal.getName() : "");
                row.createCell(2).setCellValue(animal.getSpecies() != null ? animal.getSpecies().getName() : "");
                row.createCell(3).setCellValue(animal.getBreed() != null ? animal.getBreed().getName() : "");
                row.createCell(4).setCellValue(animal.getGender() != null ? animal.getGender().name() : "");
                row.createCell(5).setCellValue(animal.getStatus() != null ? animal.getStatus().name() : "");
                row.createCell(6).setCellValue(animal.getDateOfBirth() != null ? animal.getDateOfBirth().toString() : "");
                row.createCell(7).setCellValue(animal.getBirthWeight() != null ? animal.getBirthWeight().doubleValue() : 0.0);
            }

            workbook.write(out);
            return out;
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate Herd Excel report: " + e.getMessage());
        }
    }

    @Override
    public ByteArrayOutputStream generateFinancialPdfReport(UUID organizationId) {
        List<IncomeResponse> incomes = financialService.getIncomes(organizationId);
        List<ExpenseResponse> expenses = financialService.getExpenses(organizationId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Paragraph title = new Paragraph("Financial Income & Expense Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("Generated Date: " + java.time.LocalDate.now()));
            document.add(Chunk.NEWLINE);

            // Incomes Table
            document.add(new Paragraph("Incomes Summary (" + incomes.size() + " records):", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            PdfPTable incomeTable = new PdfPTable(4);
            incomeTable.setWidthPercentage(100);
            addPdfTableHeader(incomeTable, "Date", "Category", "Description", "Amount");

            for (IncomeResponse inc : incomes) {
                incomeTable.addCell(inc.getIncomeDate() != null ? inc.getIncomeDate().toString() : "-");
                incomeTable.addCell(inc.getCategory() != null ? inc.getCategory().name() : "-");
                incomeTable.addCell(inc.getDescription());
                incomeTable.addCell(inc.getAmount() != null ? inc.getAmount().toString() : "0.00");
            }
            document.add(incomeTable);
            document.add(Chunk.NEWLINE);

            // Expenses Table
            document.add(new Paragraph("Expenses Summary (" + expenses.size() + " records):", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            PdfPTable expenseTable = new PdfPTable(4);
            expenseTable.setWidthPercentage(100);
            addPdfTableHeader(expenseTable, "Date", "Category", "Description", "Amount");

            for (ExpenseResponse exp : expenses) {
                expenseTable.addCell(exp.getExpenseDate() != null ? exp.getExpenseDate().toString() : "-");
                expenseTable.addCell(exp.getCategory() != null ? exp.getCategory().name() : "-");
                expenseTable.addCell(exp.getDescription());
                expenseTable.addCell(exp.getAmount() != null ? exp.getAmount().toString() : "0.00");
            }
            document.add(expenseTable);

            document.close();
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate Financial PDF report: " + e.getMessage());
        }

        return out;
    }

    @Override
    public ByteArrayOutputStream generateFinancialExcelReport(UUID organizationId) {
        List<IncomeResponse> incomes = financialService.getIncomes(organizationId);
        List<ExpenseResponse> expenses = financialService.getExpenses(organizationId);

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Income Sheet
            Sheet incomeSheet = workbook.createSheet("Incomes");
            Row incHeader = incomeSheet.createRow(0);
            String[] incCols = {"Date", "Category", "Description", "Amount", "Source", "Reference"};
            for (int i = 0; i < incCols.length; i++) {
                incHeader.createCell(i).setCellValue(incCols[i]);
            }
            int incRowIdx = 1;
            for (IncomeResponse inc : incomes) {
                Row row = incomeSheet.createRow(incRowIdx++);
                row.createCell(0).setCellValue(inc.getIncomeDate() != null ? inc.getIncomeDate().toString() : "");
                row.createCell(1).setCellValue(inc.getCategory() != null ? inc.getCategory().name() : "");
                row.createCell(2).setCellValue(inc.getDescription());
                row.createCell(3).setCellValue(inc.getAmount() != null ? inc.getAmount().doubleValue() : 0.0);
                row.createCell(4).setCellValue(inc.getSourceName() != null ? inc.getSourceName() : "");
                row.createCell(5).setCellValue(inc.getReferenceNumber() != null ? inc.getReferenceNumber() : "");
            }

            // Expense Sheet
            Sheet expenseSheet = workbook.createSheet("Expenses");
            Row expHeader = expenseSheet.createRow(0);
            String[] expCols = {"Date", "Category", "Description", "Amount", "Vendor", "Receipt #", "Method"};
            for (int i = 0; i < expCols.length; i++) {
                expHeader.createCell(i).setCellValue(expCols[i]);
            }
            int expRowIdx = 1;
            for (ExpenseResponse exp : expenses) {
                Row row = expenseSheet.createRow(expRowIdx++);
                row.createCell(0).setCellValue(exp.getExpenseDate() != null ? exp.getExpenseDate().toString() : "");
                row.createCell(1).setCellValue(exp.getCategory() != null ? exp.getCategory().name() : "");
                row.createCell(2).setCellValue(exp.getDescription());
                row.createCell(3).setCellValue(exp.getAmount() != null ? exp.getAmount().doubleValue() : 0.0);
                row.createCell(4).setCellValue(exp.getVendorName() != null ? exp.getVendorName() : "");
                row.createCell(5).setCellValue(exp.getReceiptNumber() != null ? exp.getReceiptNumber() : "");
                row.createCell(6).setCellValue(exp.getPaymentMethod() != null ? exp.getPaymentMethod() : "");
            }

            workbook.write(out);
            return out;
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate Financial Excel report: " + e.getMessage());
        }
    }

    private void addPdfTableHeader(PdfPTable table, String... headers) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(Color.BLUE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }
}
