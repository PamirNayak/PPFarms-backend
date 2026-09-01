package com.pamir.ppfarmsbackend.shared.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceCategoryRequest {
    @NotBlank(message = "Category type is required")
    private String categoryType;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Display label is required")
    private String displayLabel;

    private String description;
    private Integer sortOrder;
    private Boolean isActive;
}