-- ============================================================================
-- PP-FARMS CLOUD SAAS - ENTERPRISE ENHANCEMENTS MIGRATION (V2)
-- Tasks, CRM Masters, Fodder Cultivation, Documents, and Animal Lifecycle
-- ============================================================================

-- 1. ANIMAL ATTRIBUTES EXTENSION
ALTER TABLE animals 
    ADD COLUMN IF NOT EXISTS color VARCHAR(50),
    ADD COLUMN IF NOT EXISTS height NUMERIC(6, 2),
    ADD COLUMN IF NOT EXISTS purchase_price NUMERIC(10, 2),
    ADD COLUMN IF NOT EXISTS purchase_date DATE,
    ADD COLUMN IF NOT EXISTS source VARCHAR(100);

-- 2. DAILY TASK MANAGEMENT
CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    assigned_to_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    shed_pen_id UUID REFERENCES sheds_pens(id) ON DELETE SET NULL,
    animal_id UUID REFERENCES animals(id) ON DELETE SET NULL,
    task_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL', -- FEEDING, VACCINATION, CLEANING, WEIGHING, CHECKUP, GENERAL
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',   -- LOW, MEDIUM, HIGH, URGENT
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',    -- PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    due_date DATE NOT NULL,
    completed_at TIMESTAMPTZ,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_tasks_org_status ON tasks(organization_id, status);
CREATE INDEX IF NOT EXISTS idx_tasks_org_user ON tasks(organization_id, assigned_to_user_id);
CREATE INDEX IF NOT EXISTS idx_tasks_org_due ON tasks(organization_id, due_date);

-- 3. CRM MASTERS: SUPPLIERS & CUSTOMERS
CREATE TABLE IF NOT EXISTS suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(30),
    email VARCHAR(150),
    address TEXT,
    tax_id VARCHAR(50),
    payment_terms VARCHAR(100),
    opening_balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    current_balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    rating INT DEFAULT 5,
    notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_suppliers_org ON suppliers(organization_id) WHERE deleted_at IS NULL;

CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(150),
    address TEXT,
    customer_type VARCHAR(50) DEFAULT 'INDIVIDUAL', -- INDIVIDUAL, WHOLESALER, PROCESSOR, BUTCHER, COOPERATIVE
    preferred_species VARCHAR(50),
    credit_limit NUMERIC(12, 2) DEFAULT 0.00,
    outstanding_balance NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    notes TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_customers_org ON customers(organization_id) WHERE deleted_at IS NULL;

-- 4. FODDER & CROP CULTIVATION
CREATE TABLE IF NOT EXISTS crop_plots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    plot_name VARCHAR(100) NOT NULL,
    area_acres NUMERIC(6, 2) NOT NULL,
    crop_name VARCHAR(100) NOT NULL, -- NAPIER, MAIZE, LUCERNE, SORGHUM, HYBRID_GRASS, SILAGE
    variety VARCHAR(100),
    sowing_date DATE NOT NULL,
    expected_harvest_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'GROWING', -- SOWN, GROWING, READY_FOR_HARVEST, HARVESTED, FALLOW
    irrigation_type VARCHAR(50) DEFAULT 'DRIP',    -- DRIP, SPRINKLER, FLOOD, RAIN_FED
    production_cost NUMERIC(10, 2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_crops_org_status ON crop_plots(organization_id, status);

CREATE TABLE IF NOT EXISTS crop_harvest_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    crop_plot_id UUID NOT NULL REFERENCES crop_plots(id) ON DELETE CASCADE,
    harvest_date DATE NOT NULL,
    yield_kg NUMERIC(10, 2) NOT NULL,
    destination_feed_id UUID REFERENCES feed_inventory(id) ON DELETE SET NULL,
    storage_location VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_harvest_org_plot ON crop_harvest_logs(organization_id, crop_plot_id);

-- 5. DOCUMENT ATTACHMENTS (VET CERTIFICATES, RECEIPTS, REGISTRATIONS)
CREATE TABLE IF NOT EXISTS document_attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    entity_type VARCHAR(50) NOT NULL, -- ANIMAL, HEALTH, PURCHASE, SALE, FARM, VACCINE
    entity_id UUID NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(500) NOT NULL,
    file_type VARCHAR(50),
    file_size_bytes BIGINT,
    uploaded_by UUID REFERENCES users(id) ON DELETE SET NULL,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_doc_org_entity ON document_attachments(organization_id, entity_type, entity_id);
