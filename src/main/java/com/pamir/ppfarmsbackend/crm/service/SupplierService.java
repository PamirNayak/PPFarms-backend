package com.pamir.ppfarmsbackend.crm.service;

import com.pamir.ppfarmsbackend.crm.dto.SupplierRequest;
import com.pamir.ppfarmsbackend.crm.dto.SupplierResponse;

import java.util.List;
import java.util.UUID;

public interface SupplierService {
    SupplierResponse createSupplier(SupplierRequest request, UUID tenantId);
    SupplierResponse updateSupplier(UUID id, SupplierRequest request, UUID tenantId);
    SupplierResponse getSupplierById(UUID id, UUID tenantId);
    List<SupplierResponse> getAllSuppliers(UUID tenantId);
    void deleteSupplier(UUID id, UUID tenantId);
}
