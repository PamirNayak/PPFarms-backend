package com.pamir.ppfarmsbackend.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationMenuItemDto {
    private String label;
    private String path;
    private String icon;
    private String category;
    private Integer sortOrder;
}
