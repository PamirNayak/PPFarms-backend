-- ============================================================================
-- PP-FARMS CLOUD SAAS - INITIAL PRODUCTION DATABASE SCHEMA (V1)
-- Enterprise Multi-Tenant Schema matching Spring Boot Backend JPA Entities
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- 1. IDENTITY & MULTI-TENANCY CORE
-- ============================================================================

CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_org_id ON users(organization_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_email ON users(email);

-- ============================================================================
-- 2. SAAS BILLING, PLANS & SUBSCRIPTIONS
-- ============================================================================

CREATE TABLE plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    plan_type VARCHAR(30) NOT NULL, -- FREE, MONTHLY, ANNUAL
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    max_animals INT NOT NULL DEFAULT 50,
    max_users INT NOT NULL DEFAULT 2,
    features JSONB NOT NULL DEFAULT '{}'::jsonb,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES plans(id),
    status VARCHAR(30) NOT NULL DEFAULT 'TRIAL', -- TRIAL, ACTIVE, EXPIRED, UNCLAIMED, PENDING_APPROVAL
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    subscription_id UUID NOT NULL REFERENCES subscriptions(id) ON DELETE CASCADE,
    amount NUMERIC(10, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL, -- UPI_QR, BANK_TRANSFER, CASH
    transaction_ref VARCHAR(100) NOT NULL,
    receipt_image_url VARCHAR(500) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_VERIFICATION', -- PENDING_VERIFICATION, APPROVED, REJECTED
    rejection_reason TEXT,
    verified_by UUID REFERENCES users(id),
    verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    subscription_id UUID NOT NULL REFERENCES subscriptions(id),
    payment_id UUID REFERENCES payments(id),
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    amount NUMERIC(10, 2) NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pdf_url VARCHAR(500)
);

CREATE TABLE system_bank_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bank_name VARCHAR(150) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    account_holder_name VARCHAR(150) NOT NULL,
    ifsc_code VARCHAR(30) NOT NULL,
    upi_id VARCHAR(100),
    qr_code_image_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 3. SPECIES, BREEDS, HOUSING & HERD LIVESTOCK
-- ============================================================================

CREATE TABLE species (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    gestation_days INT NOT NULL,
    heat_cycle_days INT NOT NULL,
    supports_milking BOOLEAN NOT NULL DEFAULT FALSE,
    supports_shearing BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE breeds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID REFERENCES organizations(id) ON DELETE CASCADE,
    species_id UUID NOT NULL REFERENCES species(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE sheds_pens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    pen_type VARCHAR(50) NOT NULL,
    capacity INT NOT NULL DEFAULT 50,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE animals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    species_id UUID NOT NULL REFERENCES species(id),
    breed_id UUID NOT NULL REFERENCES breeds(id),
    shed_pen_id UUID REFERENCES sheds_pens(id) ON DELETE SET NULL,
    tag_number VARCHAR(50) NOT NULL,
    name VARCHAR(100),
    gender VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    date_of_birth DATE,
    birth_weight NUMERIC(7, 2),
    sire_id UUID REFERENCES animals(id) ON DELETE SET NULL,
    dam_id UUID REFERENCES animals(id) ON DELETE SET NULL,
    photo_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_animal_org_tag ON animals(organization_id, tag_number);
CREATE INDEX idx_animal_org_status ON animals(organization_id, status);

CREATE TABLE weight_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    weight_kg NUMERIC(7, 2) NOT NULL,
    measured_at DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 4. HEALTH, VACCINATION, DEWORMING & MORTALITY
-- ============================================================================

CREATE TABLE health_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    symptoms TEXT NOT NULL,
    treatment TEXT NOT NULL,
    health_status VARCHAR(30) NOT NULL, -- UNDER_TREATMENT, RECOVERED, CRITICAL, CHRONIC
    vet_name VARCHAR(100),
    treatment_date DATE NOT NULL,
    follow_up_date DATE,
    milk_withdrawal_until_date DATE,
    slaughter_withdrawal_until_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_health_org_animal ON health_records(organization_id, animal_id);

CREATE TABLE vaccination_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    vaccine_name VARCHAR(100) NOT NULL,
    batch_number VARCHAR(50),
    dosage VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    administered_at DATE NOT NULL,
    next_due_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE deworming_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    drug_name VARCHAR(100) NOT NULL,
    drug_type VARCHAR(50) NOT NULL,
    dosage VARCHAR(50) NOT NULL,
    administered_at DATE NOT NULL,
    next_due_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mortality_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    death_date DATE NOT NULL,
    cause_of_death VARCHAR(150) NOT NULL,
    necropsy_notes TEXT,
    disposal_method VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 5. REPRODUCTION & BREEDING
-- ============================================================================

CREATE TABLE heat_cycles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    observed_at DATE NOT NULL,
    expected_next_heat DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OBSERVED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE breeding_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    dam_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    sire_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    breeding_type VARCHAR(30) NOT NULL,
    bred_at DATE NOT NULL,
    outcome VARCHAR(30) NOT NULL DEFAULT 'PENDING_CONFIRMATION',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pregnancies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    breeding_record_id UUID NOT NULL REFERENCES breeding_records(id) ON DELETE CASCADE,
    animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED',
    confirmation_date DATE NOT NULL,
    expected_due_date DATE NOT NULL,
    actual_delivery_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE birth_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    pregnancy_id UUID NOT NULL REFERENCES pregnancies(id) ON DELETE CASCADE,
    dam_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    sire_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    total_born INT NOT NULL,
    alive_count INT NOT NULL,
    stillborn_count INT NOT NULL DEFAULT 0,
    birth_date DATE NOT NULL,
    delivery_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE birth_offspring (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    birth_record_id UUID NOT NULL REFERENCES birth_records(id) ON DELETE CASCADE,
    animal_id UUID NOT NULL REFERENCES animals(id) ON DELETE CASCADE,
    gender VARCHAR(20) NOT NULL,
    birth_weight NUMERIC(7, 2),
    birth_status VARCHAR(30) NOT NULL
);

-- ============================================================================
-- 6. POULTRY & FLOCK MANAGEMENT
-- ============================================================================

CREATE TABLE flock_batches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    batch_name VARCHAR(100) NOT NULL,
    species_id UUID NOT NULL REFERENCES species(id),
    shed_pen_id UUID REFERENCES sheds_pens(id) ON DELETE SET NULL,
    initial_quantity INT NOT NULL,
    current_quantity INT NOT NULL,
    arrival_date DATE NOT NULL,
    initial_age_weeks INT DEFAULT 1,
    purpose VARCHAR(30) DEFAULT 'DUAL',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    purchase_cost NUMERIC(10, 2),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_flock_org_status ON flock_batches(organization_id, status);

CREATE TABLE flock_mortality_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    flock_batch_id UUID NOT NULL REFERENCES flock_batches(id) ON DELETE CASCADE,
    dead_count INT NOT NULL,
    log_date DATE NOT NULL,
    cause_of_death VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_mortality_org_batch ON flock_mortality_logs(organization_id, flock_batch_id);

CREATE TABLE egg_production_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    flock_batch_id UUID NOT NULL REFERENCES flock_batches(id) ON DELETE CASCADE,
    collection_date DATE NOT NULL,
    total_eggs INT NOT NULL,
    broken_eggs INT DEFAULT 0,
    trays_count INT,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_eggs_org_batch ON egg_production_logs(organization_id, flock_batch_id);

-- ============================================================================
-- 7. MILK PRODUCTION & FEED MANAGEMENT
-- ============================================================================

CREATE TABLE production_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    animal_id UUID REFERENCES animals(id) ON DELETE SET NULL,
    shed_pen_id UUID REFERENCES sheds_pens(id) ON DELETE SET NULL,
    production_type VARCHAR(30) NOT NULL,
    quantity NUMERIC(10, 2) NOT NULL,
    unit VARCHAR(20) DEFAULT 'LITERS',
    fat_percentage NUMERIC(4, 2),
    snf_percentage NUMERIC(4, 2),
    recorded_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_prod_org_date ON production_records(organization_id, recorded_date);

CREATE TABLE feed_inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    feed_name VARCHAR(100) NOT NULL,
    feed_category VARCHAR(50) NOT NULL,
    quantity_kg NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    cost_per_kg NUMERIC(8, 2) NOT NULL DEFAULT 0.00,
    min_threshold_kg NUMERIC(10, 2) NOT NULL DEFAULT 50.00,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE feed_consumption_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    feed_inventory_id UUID NOT NULL REFERENCES feed_inventory(id) ON DELETE CASCADE,
    shed_pen_id UUID REFERENCES sheds_pens(id) ON DELETE SET NULL,
    quantity_consumed_kg NUMERIC(10, 2) NOT NULL,
    total_cost NUMERIC(10, 2) NOT NULL,
    consumed_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- 8. SALES, PURCHASES & FINANCIAL ACCOUNTING
-- ============================================================================

CREATE TABLE sales (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    buyer_name VARCHAR(150) NOT NULL,
    buyer_contact VARCHAR(50),
    total_amount NUMERIC(12, 2) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PAID',
    sale_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_sale_org_date ON sales(organization_id, sale_date);

CREATE TABLE sale_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sale_id UUID NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    item_type VARCHAR(50) NOT NULL,
    animal_id UUID REFERENCES animals(id) ON DELETE SET NULL,
    description VARCHAR(200) NOT NULL,
    quantity NUMERIC(10, 2) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    total_price NUMERIC(12, 2) NOT NULL
);

CREATE TABLE purchases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    invoice_number VARCHAR(50),
    vendor_name VARCHAR(150) NOT NULL,
    vendor_contact VARCHAR(50),
    total_amount NUMERIC(12, 2) NOT NULL,
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PAID',
    purchase_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_purchase_org_date ON purchases(organization_id, purchase_date);

CREATE TABLE purchase_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    purchase_id UUID NOT NULL REFERENCES purchases(id) ON DELETE CASCADE,
    item_category VARCHAR(50) NOT NULL,
    item_name VARCHAR(150) NOT NULL,
    quantity NUMERIC(10, 2) NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    subtotal_price NUMERIC(12, 2) NOT NULL
);

CREATE TABLE incomes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    income_date DATE NOT NULL,
    source_name VARCHAR(150),
    reference_number VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_income_org_date ON incomes(organization_id, income_date);

CREATE TABLE expenses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    expense_date DATE NOT NULL,
    vendor_name VARCHAR(150),
    receipt_number VARCHAR(100),
    payment_method VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_expense_org_date ON expenses(organization_id, expense_date);

-- ============================================================================
-- 9. AUDIT TRAILS, PREFERENCES, NOTIFICATIONS & SYSTEM REFERENCE
-- ============================================================================

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(30) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_created_at ON audit_logs(created_at DESC);

CREATE TABLE contact_inquiries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    farm_type VARCHAR(50) NOT NULL,
    estimated_herd_size INT,
    message TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_notification_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    notification_type VARCHAR(50) NOT NULL,
    in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE system_reference_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_type VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL,
    display_label VARCHAR(100) NOT NULL,
    description TEXT,
    sort_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- ============================================================================
-- 10. SYSTEM SEED REFERENCE DATA
-- ============================================================================

-- Core Livestock Species
INSERT INTO species (id, name, gestation_days, heat_cycle_days, supports_milking, supports_shearing)
VALUES 
    (gen_random_uuid(), 'Cattle', 283, 21, true, false),
    (gen_random_uuid(), 'Goat', 150, 21, true, false),
    (gen_random_uuid(), 'Sheep', 147, 17, true, true),
    (gen_random_uuid(), 'Buffalo', 310, 21, true, false),
    (gen_random_uuid(), 'Poultry', 21, 0, false, false),
    (gen_random_uuid(), 'Pig', 114, 21, false, false)
ON CONFLICT (name) DO NOTHING;

-- Standard Reference Categories for Dynamic Dropdowns
-- (Cleaned: Non-animal categories removed)

