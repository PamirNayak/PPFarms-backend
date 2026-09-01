package com.pamir.ppfarmsbackend.billing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_bank_accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemBankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "bank_name", nullable = false, length = 150)
    private String bankName;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "account_holder_name", nullable = false, length = 150)
    private String accountHolderName;

    @Column(name = "ifsc_code", nullable = false, length = 30)
    private String ifscCode;

    @Column(name = "upi_id", length = 100)
    private String upiId;

    @Column(name = "qr_code_image_url", columnDefinition = "TEXT")
    private String qrCodeImageUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public String getBankName() { return bankName; } public void setBankName(String bankName) { this.bankName = bankName; }
    public String getAccountNumber() { return accountNumber; } public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getAccountHolderName() { return accountHolderName; } public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    public String getIfscCode() { return ifscCode; } public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getUpiId() { return upiId; } public void setUpiId(String upiId) { this.upiId = upiId; }
    public String getQrCodeImageUrl() { return qrCodeImageUrl; } public void setQrCodeImageUrl(String qrCodeImageUrl) { this.qrCodeImageUrl = qrCodeImageUrl; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
