package com.pamir.ppfarmsbackend.shared.dto;

import com.pamir.ppfarmsbackend.shared.entity.SystemReferenceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceCategoryResponse {
    private UUID id;
    private String categoryType;
    private String code;
    private String displayLabel;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;

    public static ReferenceCategoryResponse fromEntity(SystemReferenceCategory cat) {
        if (cat == null) return null;
        return ReferenceCategoryResponse.builder()
                .id(cat.getId())
                .categoryType(cat.getCategoryType())
                .code(cat.getCode())
                .displayLabel(cat.getDisplayLabel())
                .description(cat.getDescription())
                .sortOrder(cat.getSortOrder())
                .isActive(cat.getIsActive())
                .build();
    }
}