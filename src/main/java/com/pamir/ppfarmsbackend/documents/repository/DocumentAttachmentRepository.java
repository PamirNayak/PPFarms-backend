package com.pamir.ppfarmsbackend.documents.repository;

import com.pamir.ppfarmsbackend.documents.entity.DocumentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentAttachmentRepository extends JpaRepository<DocumentAttachment, UUID> {
    List<DocumentAttachment> findByOrganizationIdAndEntityTypeAndEntityIdOrderByCreatedAtDesc(UUID organizationId, String entityType, UUID entityId);
    List<DocumentAttachment> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
}
