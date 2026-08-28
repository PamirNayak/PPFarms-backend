package com.pamir.ppfarmsbackend.crm.service.impl;

import com.pamir.ppfarmsbackend.crm.dto.SupplierRequest;
import com.pamir.ppfarmsbackend.crm.dto.SupplierResponse;
import com.pamir.ppfarmsbackend.crm.entity.Supplier;
import com.pamir.ppfarmsbackend.crm.repository.SupplierRepository;
import com.pamir.ppfarmsbackend.crm.service.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public SupplierResponse createSupplier(SupplierRequest request, UUID tenantId) {
        Supplier supplier = Supplier.builder()
                .organizationId(tenantId)
                .name(request.getName())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .taxId(request.getTaxId())
                .paymentTerms(request.getPaymentTerms())
                .openingBalance(request.getOpeningBalance() != null ? request.getOpeningBalance() : BigDecimal.ZERO)
                .currentBalance(request.getOpeningBalance() != null ? request.getOpeningBalance() : BigDecimal.ZERO)
                .rating(request.getRating() != null ? request.getRating() : 5)
                .notes(request.getNotes())
                .isActive(true)
                .build();

        supplier = supplierRepository.save(supplier);
        return mapToResponse(supplier);
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(UUID id, SupplierRequest request, UUID tenantId) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        if (!supplier.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        supplier.setName(request.getName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());
        supplier.setTaxId(request.getTaxId());
        supplier.setPaymentTerms(request.getPaymentTerms());
        if (request.getRating() != null) supplier.setRating(request.getRating());
        supplier.setNotes(request.getNotes());

        supplier = supplierRepository.save(supplier);
        return mapToResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(UUID id, UUID tenantId) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        if (!supplier.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        return mapToResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers(UUID tenantId) {
        return supplierRepository.findByOrganizationIdAndDeletedAtIsNullOrderByNameAsc(tenantId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public void deleteSupplier(UUID id, UUID tenantId) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        if (!supplier.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        supplierRepository.delete(supplier);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .organizationId(supplier.getOrganizationId())
                .name(supplier.getName())
                .contactPerson(supplier.getContactPerson())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .address(supplier.getAddress())
                .taxId(supplier.getTaxId())
                .paymentTerms(supplier.getPaymentTerms())
                .openingBalance(supplier.getOpeningBalance())
                .currentBalance(supplier.getCurrentBalance())
                .rating(supplier.getRating())
                .notes(supplier.getNotes())
                .isActive(supplier.getIsActive())
                .createdAt(supplier.getCreatedAt())
                .build();
    }
}
