package com.pamir.ppfarmsbackend.purchases.service.impl;

import com.pamir.ppfarmsbackend.purchases.dto.PurchaseItemDto;
import com.pamir.ppfarmsbackend.purchases.dto.PurchaseRequest;
import com.pamir.ppfarmsbackend.purchases.dto.PurchaseResponse;
import com.pamir.ppfarmsbackend.purchases.entity.Purchase;
import com.pamir.ppfarmsbackend.purchases.entity.PurchaseItem;
import com.pamir.ppfarmsbackend.purchases.repository.PurchaseItemRepository;
import com.pamir.ppfarmsbackend.purchases.repository.PurchaseRepository;
import com.pamir.ppfarmsbackend.purchases.service.PurchaseService;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request, UUID tenantId) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseItem> itemsToSave = new ArrayList<>();

        for (PurchaseItemDto itemDto : request.getItems()) {
            BigDecimal subtotal = itemDto.getQuantity().multiply(itemDto.getUnitPrice());
            totalAmount = totalAmount.add(subtotal);

            PurchaseItem item = PurchaseItem.builder()
                    .itemCategory(itemDto.getItemCategory().toUpperCase())
                    .itemName(itemDto.getItemName())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .subtotalPrice(subtotal)
                    .build();
            itemsToSave.add(item);
        }

        Purchase purchase = Purchase.builder()
                .organizationId(tenantId)
                .invoiceNumber(request.getInvoiceNumber())
                .vendorName(request.getVendorName())
                .vendorContact(request.getVendorContact())
                .totalAmount(totalAmount)
                .paymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus().toUpperCase() : "PAID")
                .purchaseDate(request.getPurchaseDate())
                .notes(request.getNotes())
                .build();

        purchase = purchaseRepository.save(purchase);

        for (PurchaseItem item : itemsToSave) {
            item.setPurchaseId(purchase.getId());
        }
        purchaseItemRepository.saveAll(itemsToSave);

        return mapToResponse(purchase, itemsToSave);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseResponse getPurchaseById(UUID id, UUID tenantId) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase record not found"));

        if (!purchase.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(purchase.getId());
        return mapToResponse(purchase, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseResponse> getPurchases(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        List<Purchase> purchases;
        if (startDate != null && endDate != null) {
            purchases = purchaseRepository.findByOrganizationIdAndPurchaseDateBetweenAndDeletedAtIsNull(tenantId, startDate, endDate);
        } else {
            purchases = purchaseRepository.findByOrganizationIdAndDeletedAtIsNull(tenantId);
        }

        return purchases.stream()
                .map(p -> {
                    List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(p.getId());
                    return mapToResponse(p, items);
                })
                .toList();
    }

    private PurchaseResponse mapToResponse(Purchase purchase, List<PurchaseItem> items) {
        List<PurchaseItemDto> itemDtos = items.stream().map(i -> {
            PurchaseItemDto dto = new PurchaseItemDto();
            dto.setId(i.getId());
            dto.setItemCategory(i.getItemCategory());
            dto.setItemName(i.getItemName());
            dto.setQuantity(i.getQuantity());
            dto.setUnitPrice(i.getUnitPrice());
            dto.setSubtotalPrice(i.getSubtotalPrice());
            return dto;
        }).toList();

        return PurchaseResponse.builder()
                .id(purchase.getId())
                .organizationId(purchase.getOrganizationId())
                .invoiceNumber(purchase.getInvoiceNumber())
                .vendorName(purchase.getVendorName())
                .vendorContact(purchase.getVendorContact())
                .totalAmount(purchase.getTotalAmount())
                .paymentStatus(purchase.getPaymentStatus())
                .purchaseDate(purchase.getPurchaseDate())
                .notes(purchase.getNotes())
                .items(itemDtos)
                .createdAt(purchase.getCreatedAt())
                .build();
    }
}
