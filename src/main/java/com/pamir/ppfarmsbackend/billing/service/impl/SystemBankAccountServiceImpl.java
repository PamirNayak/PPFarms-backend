package com.pamir.ppfarmsbackend.billing.service.impl;

import com.pamir.ppfarmsbackend.billing.dto.SystemBankAccountRequest;
import com.pamir.ppfarmsbackend.billing.dto.SystemBankAccountResponse;
import com.pamir.ppfarmsbackend.billing.entity.SystemBankAccount;
import com.pamir.ppfarmsbackend.billing.repository.SystemBankAccountRepository;
import com.pamir.ppfarmsbackend.billing.service.SystemBankAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SystemBankAccountServiceImpl implements SystemBankAccountService {

    private final SystemBankAccountRepository bankAccountRepository;

    @Override
    public List<SystemBankAccountResponse> getActiveBankAccounts() {
        return bankAccountRepository.findByIsActiveTrue()
                .stream()
                .map(SystemBankAccountResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public SystemBankAccountResponse addBankAccount(SystemBankAccountRequest request) {
        SystemBankAccount bankAccount = SystemBankAccount.builder()
                .bankName(request.getBankName())
                .accountHolderName(request.getAccountHolderName())
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode())
                .upiId(request.getUpiId())
                .qrCodeImageUrl(request.getQrCodeImageUrl())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return SystemBankAccountResponse.fromEntity(bankAccountRepository.save(bankAccount));
    }
}