package com.pamir.ppfarmsbackend.herd.entity;

import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "sheds_pens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShedPen extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "pen_type", nullable = false, length = 50)
    private String penType;

    @Column(nullable = false)
    @Builder.Default
    private Integer capacity = 50;

    public UUID getOrganizationId() { return organizationId; } public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getPenType() { return penType; } public void setPenType(String penType) { this.penType = penType; }
    public Integer getCapacity() { return capacity; } public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public static ShedPenBuilder builder() { return new ShedPenBuilder(); }
    public static class ShedPenBuilder {
        private final ShedPen sp = new ShedPen();
        public ShedPenBuilder id(UUID id) { sp.setId(id); return this; }
        public ShedPenBuilder organizationId(UUID organizationId) { sp.setOrganizationId(organizationId); return this; }
        public ShedPenBuilder name(String name) { sp.setName(name); return this; }
        public ShedPenBuilder penType(String penType) { sp.setPenType(penType); return this; }
        public ShedPenBuilder capacity(Integer capacity) { sp.setCapacity(capacity); return this; }
        public ShedPen build() { return sp; }
    }
}
