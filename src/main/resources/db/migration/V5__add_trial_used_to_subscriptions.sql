-- Add trial_used boolean column to track one-time free trial usage per farm organization
ALTER TABLE subscriptions ADD COLUMN IF NOT EXISTS trial_used BOOLEAN NOT NULL DEFAULT TRUE;