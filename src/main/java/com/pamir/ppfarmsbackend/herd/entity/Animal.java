package com.pamir.ppfarmsbackend.herd.entity;

import com.pamir.ppfarmsbackend.herd.domain.AnimalGender;
import com.pamir.ppfarmsbackend.herd.domain.AnimalStatus;
import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "animals", indexes = {
    @Index(name = "idx_animal_org_tag", columnList = "organization_id, tag_number"),
    @Index(name = "idx_animal_org_status", columnList = "organization_id, status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Animal extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "breed_id", nullable = false)
    private Breed breed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shed_pen_id")
    private ShedPen shedPen;

    @Column(name = "tag_number", nullable = false, length = 50)
    private String tagNumber;

    @Column(length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnimalGender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private AnimalStatus status = AnimalStatus.ACTIVE;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "birth_weight", precision = 7, scale = 2)
    private BigDecimal birthWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sire_id")
    private Animal sire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dam_id")
    private Animal dam;

    @Column(length = 50)
    private String color;

    @Column(precision = 6, scale = 2)
    private BigDecimal height;

    @Column(name = "purchase_price", precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(length = 100)
    private String source;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;
}
