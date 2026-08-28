package com.pamir.ppfarmsbackend.sales.service;

import com.pamir.ppfarmsbackend.sales.dto.SaleRequest;
import com.pamir.ppfarmsbackend.sales.dto.SaleResponse;

import java.util.List;
import java.util.UUID;

public interface SaleService {

    SaleResponse createSale(UUID organizationId, SaleRequest request);

    List<SaleResponse> getSales(UUID organizationId);

    SaleResponse getSaleById(UUID organizationId, UUID saleId);
}
