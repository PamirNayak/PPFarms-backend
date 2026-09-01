package com.pamir.ppfarmsbackend.shared.service;

import com.pamir.ppfarmsbackend.shared.dto.ReferenceCategoryRequest;
import com.pamir.ppfarmsbackend.shared.dto.ReferenceCategoryResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReferenceMetadataService {
    Map<String, Object> getFullReferenceMetadata();
    List<Map<String, String>> getSystemModules();
    Map<String, List<ReferenceCategoryResponse>> getReferenceCategories();
    ReferenceCategoryResponse createCategory(ReferenceCategoryRequest request);
    ReferenceCategoryResponse updateCategory(UUID id, ReferenceCategoryRequest details);
    void deleteCategory(UUID id);
}