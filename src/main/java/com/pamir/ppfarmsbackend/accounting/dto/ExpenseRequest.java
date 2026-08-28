package com.pamir.ppfarmsbackend.accounting.dto;

import com.pamir.ppfarmsbackend.accounting.enums.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {

    @NotNull(message = "Expense category is required")
    private ExpenseCategory category;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;

    private String vendorName;
    private String receiptNumber;
    private String paymentMethod;

    public ExpenseCategory getCategory() { return category; } public void setCategory(ExpenseCategory category) { this.category = category; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getExpenseDate() { return expenseDate; } public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public String getVendorName() { return vendorName; } public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getReceiptNumber() { return receiptNumber; } public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }
    public String getPaymentMethod() { return paymentMethod; } public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public static ExpenseRequestBuilder builder() { return new ExpenseRequestBuilder(); }
    public static class ExpenseRequestBuilder {
        private final ExpenseRequest r = new ExpenseRequest();
        public ExpenseRequestBuilder category(ExpenseCategory category) { r.setCategory(category); return this; }
        public ExpenseRequestBuilder description(String description) { r.setDescription(description); return this; }
        public ExpenseRequestBuilder amount(BigDecimal amount) { r.setAmount(amount); return this; }
        public ExpenseRequestBuilder expenseDate(LocalDate expenseDate) { r.setExpenseDate(expenseDate); return this; }
        public ExpenseRequestBuilder vendorName(String vendorName) { r.setVendorName(vendorName); return this; }
        public ExpenseRequestBuilder receiptNumber(String receiptNumber) { r.setReceiptNumber(receiptNumber); return this; }
        public ExpenseRequestBuilder paymentMethod(String paymentMethod) { r.setPaymentMethod(paymentMethod); return this; }
        public ExpenseRequest build() { return r; }
    }
}
