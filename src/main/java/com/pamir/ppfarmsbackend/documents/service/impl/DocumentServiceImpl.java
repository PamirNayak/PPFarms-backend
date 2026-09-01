package com.pamir.ppfarmsbackend.documents.service.impl;

import com.pamir.ppfarmsbackend.documents.dto.DocumentRequest;
import com.pamir.ppfarmsbackend.documents.dto.DocumentResponse;
import com.pamir.ppfarmsbackend.documents.entity.DocumentAttachment;
import com.pamir.ppfarmsbackend.documents.repository.DocumentAttachmentRepository;
import com.pamir.ppfarmsbackend.documents.service.DocumentService;
import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.identity.repository.UserRepository;
import com.pamir.ppfarmsbackend.shared.exception.BadRequestException;
import com.pamir.ppfarmsbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentAttachmentRepository documentAttachmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DocumentResponse attachDocument(DocumentRequest request, UUID userId, UUID tenantId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        DocumentAttachment doc = DocumentAttachment.builder()
                .organizationId(tenantId)
                .entityType(request.getEntityType().toUpperCase())
                .entityId(request.getEntityId())
                .fileName(request.getFileName())
                .fileUrl(request.getFileUrl())
                .fileType(request.getFileType() != null ? request.getFileType().toUpperCase() : "PDF")
                .fileSizeBytes(request.getFileSizeBytes())
                .uploadedBy(user)
                .notes(request.getNotes())
                .build();

        doc = documentAttachmentRepository.save(doc);
        return mapToResponse(doc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByEntity(String entityType, UUID entityId, UUID tenantId) {
        return documentAttachmentRepository.findByOrganizationIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(tenantId, entityType.toUpperCase(), entityId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getAllDocuments(UUID tenantId) {
        return documentAttachmentRepository.findByOrganizationIdOrderByCreatedAtDesc(tenantId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public void deleteDocument(UUID id, UUID tenantId) {
        DocumentAttachment doc = documentAttachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document attachment not found"));

        if (!doc.getOrganizationId().equals(tenantId)) {
            throw new BadRequestException("Unauthorized access");
        }

        documentAttachmentRepository.delete(doc);
    }

    private DocumentResponse mapToResponse(DocumentAttachment doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .organizationId(doc.getOrganizationId())
                .entityType(doc.getEntityType())
                .entityId(doc.getEntityId())
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .fileType(doc.getFileType())
                .fileSizeBytes(doc.getFileSizeBytes())
                .uploadedByUserId(doc.getUploadedBy() != null ? doc.getUploadedBy().getId() : null)
                .uploadedByUserName(doc.getUploadedBy() != null ? doc.getUploadedBy().getName() : null)
                .notes(doc.getNotes())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
