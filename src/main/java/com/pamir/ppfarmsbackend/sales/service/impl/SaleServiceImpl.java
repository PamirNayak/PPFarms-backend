package com.pamir.ppfarmsbackend.sales.service.impl;

import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;

import com.pamir.ppfarmsbackend.herd.repository.AnimalRepository;
import com.pamir.ppfarmsbackend.herd.entity.Animal;

import com.pamir.ppfarmsbackend.health.repository.HealthRepository;
import com.pamir.ppfarmsbackend.health.entity.HealthRecord;

import com.pamir.ppfarmsbackend.sales.dto.SaleItemDto;
import com.pamir.ppfarmsbackend.sales.dto.SaleRequest;
import com.pamir.ppfarmsbackend.sales.dto.SaleResponse;
import com.pamir.ppfarmsbackend.sales.entity.Sale;
import com.pamir.ppfarmsbackend.sales.entity.SaleItem;
import com.pamir.ppfarmsbackend.sales.repository.SaleItemRepository;
import com.pamir.ppfarmsbackend.sales.repository.SaleRepository;
import com.pamir.ppfarmsbackend.sales.service.SaleService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final AnimalRepository animalRepository;
    private final HealthRepository healthRepository;

    public SaleServiceImpl(SaleRepository saleRepository,
                           SaleItemRepository saleItemRepository,
                           AnimalRepository animalRepository,
                           HealthRepository healthRepository) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.animalRepository = animalRepository;
        this.healthRepository = healthRepository;
    }

    @Override
    public SaleResponse createSale(UUID organizationId, SaleRequest request) {
        // 1. Food Safety Compliance Check
        LocalDate checkDate = request.getSaleDate() != null ? request.getSaleDate() : LocalDate.now();

        for (SaleItemDto item : request.getItems()) {
            if ("ANIMAL".equalsIgnoreCase(item.getItemType()) || item.getAnimalId() != null) {
                if (item.getAnimalId() != null) {
                    List<HealthRecord> healthRecords = healthRepository.findByOrganizationIdAndAnimalId(organizationId, item.getAnimalId());
                    for (HealthRecord hr : healthRecords) {
                        if (hr.getSlaughterWithdrawalUntilDate() != null && !hr.getSlaughterWithdrawalUntilDate().isBefore(checkDate)) {
                            String tag = getAnimalTag(item.getAnimalId());
                            throw new BadRequestException("FOOD SAFETY BLOCK: Cannot sell animal (Tag: " + tag +
                                    ") because it is currently under medical drug withdrawal for slaughter until " + hr.getSlaughterWithdrawalUntilDate());
                        }
                    }
                }
            } else if ("MILK".equalsIgnoreCase(item.getItemType())) {
                if (item.getAnimalId() != null) {
                    List<HealthRecord> healthRecords = healthRepository.findByOrganizationIdAndAnimalId(organizationId, item.getAnimalId());
                    for (HealthRecord hr : healthRecords) {
                        if (hr.getMilkWithdrawalUntilDate() != null && !hr.getMilkWithdrawalUntilDate().isBefore(checkDate)) {
                            String tag = getAnimalTag(item.getAnimalId());
                            throw new BadRequestException("FOOD SAFETY BLOCK: Cannot sell milk from animal (Tag: " + tag +
                                    ") because it is currently under medical drug withdrawal for milk until " + hr.getMilkWithdrawalUntilDate());
                        }
                    }
                }
            }
        }

        // 2. Generate unique invoice number
        String invoiceNumber = generateInvoiceNumber();

        // 3. Compute items and total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SaleItem> saleItemsToSave = new ArrayList<>();

        for (SaleItemDto itemDto : request.getItems()) {
            BigDecimal itemTotal = itemDto.getQuantity().multiply(itemDto.getUnitPrice());
            totalAmount = totalAmount.add(itemTotal);

            SaleItem item = SaleItem.builder()
                    .itemType(itemDto.getItemType().toUpperCase())
                    .animalId(itemDto.getAnimalId())
                    .description(itemDto.getDescription())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .totalPrice(itemTotal)
                    .build();
            saleItemsToSave.add(item);
        }

        // 4. Save Sale entity
        Sale sale = Sale.builder()
                .organizationId(organizationId)
                .invoiceNumber(invoiceNumber)
                .buyerName(request.getBuyerName())
                .buyerContact(request.getBuyerContact())
                .totalAmount(totalAmount)
                .paymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : "PAID")
                .saleDate(request.getSaleDate())
                .notes(request.getNotes())
                .build();

        Sale savedSale = saleRepository.save(sale);

        // 5. Attach sale reference & save items
        for (SaleItem item : saleItemsToSave) {
            item.setSale(savedSale);
        }
        List<SaleItem> savedItems = saleItemRepository.saveAll(saleItemsToSave);

        return mapToResponse(savedSale, savedItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getSales(UUID organizationId) {
        List<Sale> sales = saleRepository.findByOrganizationIdOrderBySaleDateDesc(organizationId);
        return sales.stream().map(s -> {
            List<SaleItem> items = saleItemRepository.findBySaleId(s.getId());
            return mapToResponse(s, items);
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(UUID organizationId, UUID saleId) {
        Sale sale = saleRepository.findByIdAndOrganizationId(saleId, organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale invoice not found"));
        List<SaleItem> items = saleItemRepository.findBySaleId(sale.getId());
        return mapToResponse(sale, items);
    }

    private String generateInvoiceNumber() {
        String code;
        do {
            code = "INV-SALE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (saleRepository.existsByInvoiceNumber(code));
        return code;
    }

    private String getAnimalTag(UUID animalId) {
        return animalRepository.findById(animalId)
                .map(Animal::getTagNumber)
                .orElse(animalId.toString());
    }

    private SaleResponse mapToResponse(Sale sale, List<SaleItem> items) {
        List<SaleItemDto> itemDtos = items.stream().map(i -> SaleItemDto.builder()
                .id(i.getId())
                .itemType(i.getItemType())
                .animalId(i.getAnimalId())
                .description(i.getDescription())
                .quantity(i.getQuantity())
                .unitPrice(i.getUnitPrice())
                .totalPrice(i.getTotalPrice())
                .build()
        ).toList();

        return SaleResponse.builder()
                .id(sale.getId())
                .organizationId(sale.getOrganizationId())
                .invoiceNumber(sale.getInvoiceNumber())
                .buyerName(sale.getBuyerName())
                .buyerContact(sale.getBuyerContact())
                .totalAmount(sale.getTotalAmount())
                .paymentStatus(sale.getPaymentStatus())
                .saleDate(sale.getSaleDate())
                .notes(sale.getNotes())
                .items(itemDtos)
                .createdAt(sale.getCreatedAt())
                .build();
    }
}
