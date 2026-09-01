package com.pamir.ppfarmsbackend.documents.entity;

import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "document_attachments", indexes = {
    @Index(name = "idx_doc_org_entity", columnList = "organization_id, entity_type, entity_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentAttachment extends BaseEntity {

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // ANIMAL, HEALTH, PURCHASE, SALE, FARM, VACCINE

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Column(name = "file_type", length = 50)
    private String fileType; // PDF, PNG, JPEG, DOCX

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
