package com.pamir.ppfarmsbackend.billing.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "plans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "plan_type", nullable = false, length = 30)
    private String planType; // FREE, MONTHLY, ANNUAL

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "max_animals", nullable = false)
    @Builder.Default
    private Integer maxAnimals = 50;

    @Column(name = "max_users", nullable = false)
    @Builder.Default
    private Integer maxUsers = 2;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private String features = "{}";

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public UUID getId() { return id; } public void setId(UUID id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getPlanType() { return planType; } public void setPlanType(String planType) { this.planType = planType; }
    public BigDecimal getPrice() { return price; } public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getMaxAnimals() { return maxAnimals; } public void setMaxAnimals(Integer maxAnimals) { this.maxAnimals = maxAnimals; }
    public Integer getMaxUsers() { return maxUsers; } public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }
    public String getFeatures() { return features; } public void setFeatures(String features) { this.features = features; }
    public Boolean getIsActive() { return isActive; } public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
