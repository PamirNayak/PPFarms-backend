package com.pamir.ppfarmsbackend.billing.service;

import com.pamir.ppfarmsbackend.billing.dto.SystemBankAccountRequest;
import com.pamir.ppfarmsbackend.billing.dto.SystemBankAccountResponse;

import java.util.List;

public interface SystemBankAccountService {
    List<SystemBankAccountResponse> getActiveBankAccounts();
    SystemBankAccountResponse addBankAccount(SystemBankAccountRequest request);
}