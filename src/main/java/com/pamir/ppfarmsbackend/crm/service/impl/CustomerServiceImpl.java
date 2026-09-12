package com.pamir.ppfarmsbackend.crm.service.impl;

import com.pamir.ppfarmsbackend.crm.dto.CustomerRequest;
import com.pamir.ppfarmsbackend.crm.dto.CustomerResponse;
import com.pamir.ppfarmsbackend.crm.entity.Customer;
import com.pamir.ppfarmsbackend.crm.repository.CustomerRepository;
import com.pamir.ppfarmsbackend.crm.service.CustomerService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request, UUID tenantId) {
        Customer customer = Customer.builder()
                .organizationId(tenantId)
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .customerType(request.getCustomerType() != null ? request.getCustomerType() : "INDIVIDUAL")
                .preferredSpecies(request.getPreferredSpecies())
                .creditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : BigDecimal.ZERO)
                .outstandingBalance(BigDecimal.ZERO)
                .notes(request.getNotes())
                .isActive(true)
                .build();

        customer = customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request, UUID tenantId) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!customer.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        if (request.getCustomerType() != null) customer.setCustomerType(request.getCustomerType());
        customer.setPreferredSpecies(request.getPreferredSpecies());
        if (request.getCreditLimit() != null) customer.setCreditLimit(request.getCreditLimit());
        customer.setNotes(request.getNotes());

        customer = customerRepository.save(customer);
        return mapToResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id, UUID tenantId) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!customer.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        return mapToResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers(UUID tenantId) {
        return customerRepository.findByOrganizationIdAndDeletedAtIsNullOrderByNameAsc(tenantId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID id, UUID tenantId) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!customer.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        if (customer.getOutstandingBalance() != null && customer.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Cannot delete customer '" + customer.getName() + "' because they have an outstanding balance of " + customer.getOutstandingBalance());
        }

        customer.setDeletedAt(java.time.OffsetDateTime.now());
        customer.setIsActive(false);
        customerRepository.save(customer);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .organizationId(customer.getOrganizationId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .customerType(customer.getCustomerType())
                .preferredSpecies(customer.getPreferredSpecies())
                .creditLimit(customer.getCreditLimit())
                .outstandingBalance(customer.getOutstandingBalance())
                .notes(customer.getNotes())
                .isActive(customer.getIsActive())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}
