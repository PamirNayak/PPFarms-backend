package com.pamir.ppfarmsbackend.shared.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "system_reference_categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemReferenceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String categoryType; // EXPENSE_CATEGORY, INCOME_CATEGORY, HEALTH_STATUS, BREEDING_TYPE, SALE_ITEM_TYPE, PURCHASE_ITEM_CATEGORY, ANIMAL_STATUS, ANIMAL_GENDER, PLAN_FEATURE

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String displayLabel;

    private String description;

    @Builder.Default
    private Integer sortOrder = 0;

    @Builder.Default
    private Boolean isActive = true;
}
