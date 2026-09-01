package com.pamir.ppfarmsbackend.documents.service;

import com.pamir.ppfarmsbackend.documents.dto.DocumentRequest;
import com.pamir.ppfarmsbackend.documents.dto.DocumentResponse;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentResponse attachDocument(DocumentRequest request, UUID userId, UUID tenantId);
    List<DocumentResponse> getDocumentsByEntity(String entityType, UUID entityId, UUID tenantId);
    List<DocumentResponse> getAllDocuments(UUID tenantId);
    void deleteDocument(UUID id, UUID tenantId);
}
