-- ============================================================================
-- V6: Add target_plan_id to payments table
-- Ensures payments hold an explicit reference to the requested plan before approval
-- ============================================================================

ALTER TABLE payments
ADD COLUMN IF NOT EXISTS target_plan_id UUID REFERENCES plans(id);

CREATE INDEX IF NOT EXISTS idx_payments_target_plan ON payments(target_plan_id);
