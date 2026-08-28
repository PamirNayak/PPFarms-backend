package com.pamir.ppfarmsbackend.accounting.entity;

import com.pamir.ppfarmsbackend.accounting.enums.IncomeCategory;
import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "incomes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Income extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private IncomeCategory category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "income_date", nullable = false)
    private LocalDate incomeDate;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "reference_number")
    private String referenceNumber;
}