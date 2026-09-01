package com.pamir.ppfarmsbackend.billing.dto;

import com.pamir.ppfarmsbackend.billing.entity.SystemBankAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemBankAccountResponse {
    private UUID id;
    private String bankName;
    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    private String upiId;
    private String qrCodeImageUrl;
    private Boolean isActive;
    private OffsetDateTime createdAt;

    public static SystemBankAccountResponse fromEntity(SystemBankAccount b) {
        if (b == null) return null;
        return SystemBankAccountResponse.builder()
                .id(b.getId())
                .bankName(b.getBankName())
                .accountHolderName(b.getAccountHolderName())
                .accountNumber(b.getAccountNumber())
                .ifscCode(b.getIfscCode())
                .upiId(b.getUpiId())
                .qrCodeImageUrl(b.getQrCodeImageUrl())
                .isActive(b.getIsActive())
                .createdAt(b.getCreatedAt())
                .build();
    }
}