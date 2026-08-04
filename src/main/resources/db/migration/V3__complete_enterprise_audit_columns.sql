-- ============================================================================
-- PP-FARMS CLOUD SAAS - AUDIT & SOFT DELETE COLUMNS ALIGNMENT (V3)
-- Aligns crop_harvest_logs and document_attachments with BaseEntity mapped superclass
-- ============================================================================

ALTER TABLE crop_harvest_logs
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

ALTER TABLE document_attachments
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;
