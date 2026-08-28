package com.pamir.ppfarmsbackend.purchases.service;

import com.pamir.ppfarmsbackend.purchases.dto.PurchaseRequest;
import com.pamir.ppfarmsbackend.purchases.dto.PurchaseResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest request, UUID tenantId);
    PurchaseResponse getPurchaseById(UUID id, UUID tenantId);
    List<PurchaseResponse> getPurchases(UUID tenantId, LocalDate startDate, LocalDate endDate);
}
