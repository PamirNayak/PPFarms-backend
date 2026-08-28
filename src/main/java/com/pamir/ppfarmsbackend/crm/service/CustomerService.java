package com.pamir.ppfarmsbackend.crm.service;

import com.pamir.ppfarmsbackend.crm.dto.CustomerRequest;
import com.pamir.ppfarmsbackend.crm.dto.CustomerResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request, UUID tenantId);
    CustomerResponse updateCustomer(UUID id, CustomerRequest request, UUID tenantId);
    CustomerResponse getCustomerById(UUID id, UUID tenantId);
    List<CustomerResponse> getAllCustomers(UUID tenantId);
    void deleteCustomer(UUID id, UUID tenantId);
}
