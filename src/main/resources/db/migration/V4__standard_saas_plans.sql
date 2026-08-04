-- ============================================================================
-- PP-FARMS CLOUD SAAS - MIGRATION V4
-- Description: Standardize SaaS Subscription Plans and default Animal Types
-- ============================================================================

-- 1. Ensure Standard Animal Types exist in system_reference_categories
INSERT INTO system_reference_categories (id, category_type, code, display_label, sort_order, is_active)
VALUES
    (gen_random_uuid(), 'ANIMAL_TYPE', 'GOAT', 'Goat (Capra hircus)', 1, true),
    (gen_random_uuid(), 'ANIMAL_TYPE', 'SHEEP', 'Sheep (Ovis aries)', 2, true),
    (gen_random_uuid(), 'ANIMAL_TYPE', 'COW', 'Dairy Cattle / Cow', 3, true),
    (gen_random_uuid(), 'ANIMAL_TYPE', 'BUFFALO', 'Dairy Buffalo', 4, true),
    (gen_random_uuid(), 'ANIMAL_TYPE', 'POULTRY', 'Poultry & Birds', 5, true)
ON CONFLICT DO NOTHING;

-- 2. Insert or Update Standard SaaS Subscription Plans
-- Free Trial Plan (14 Days)
INSERT INTO plans (id, name, plan_type, price, max_animals, max_users, features, is_active)
VALUES (
    gen_random_uuid(),
    'Free Trial (14-Day)',
    'FREE',
    0.00,
    50,
    2,
    '{"milkLogging":true,"pedigree":true,"vetModule":true,"accounting":false,"reports":false,"allowedSpecies":["GOAT","SHEEP","COW","BUFFALO","POULTRY"],"multiSpecies":true,"maxSpeciesCount":5}'::jsonb,
    true
)
ON CONFLICT (name) DO UPDATE SET
    plan_type = EXCLUDED.plan_type,
    price = EXCLUDED.price,
    max_animals = EXCLUDED.max_animals,
    max_users = EXCLUDED.max_users,
    features = EXCLUDED.features,
    is_active = EXCLUDED.is_active;

-- Commercial Starter Monthly Plan
INSERT INTO plans (id, name, plan_type, price, max_animals, max_users, features, is_active)
VALUES (
    gen_random_uuid(),
    'Commercial Starter (Monthly)',
    'MONTHLY',
    999.00,
    200,
    5,
    '{"milkLogging":true,"pedigree":true,"vetModule":true,"accounting":true,"reports":true,"allowedSpecies":["GOAT","SHEEP","COW","BUFFALO","POULTRY"],"multiSpecies":true,"maxSpeciesCount":5}'::jsonb,
    true
)
ON CONFLICT (name) DO UPDATE SET
    plan_type = EXCLUDED.plan_type,
    price = EXCLUDED.price,
    max_animals = EXCLUDED.max_animals,
    max_users = EXCLUDED.max_users,
    features = EXCLUDED.features,
    is_active = EXCLUDED.is_active;

-- Enterprise Scale Annual Plan
INSERT INTO plans (id, name, plan_type, price, max_animals, max_users, features, is_active)
VALUES (
    gen_random_uuid(),
    'Enterprise Scale (Annual)',
    'ANNUAL',
    9999.00,
    999999,
    20,
    '{"milkLogging":true,"pedigree":true,"vetModule":true,"accounting":true,"reports":true,"allowedSpecies":["GOAT","SHEEP","COW","BUFFALO","POULTRY"],"multiSpecies":true,"maxSpeciesCount":5}'::jsonb,
    true
)
ON CONFLICT (name) DO UPDATE SET
    plan_type = EXCLUDED.plan_type,
    price = EXCLUDED.price,
    max_animals = EXCLUDED.max_animals,
    max_users = EXCLUDED.max_users,
    features = EXCLUDED.features,
    is_active = EXCLUDED.is_active;

-- 3. Remap Any Existing Subscriptions to the New Standard Plan
UPDATE subscriptions
SET plan_id = (SELECT id FROM plans WHERE name = 'Free Trial (14-Day)' LIMIT 1)
WHERE plan_id NOT IN (
    SELECT id FROM plans WHERE name IN ('Free Trial (14-Day)', 'Commercial Starter (Monthly)', 'Enterprise Scale (Annual)')
);

-- 4. Delete Any Legacy Plans That Are Not the New Standard Plans
DELETE FROM plans
WHERE name NOT IN ('Free Trial (14-Day)', 'Commercial Starter (Monthly)', 'Enterprise Scale (Annual)');

-- 5. Ensure All Standard Plans are Active
UPDATE plans
SET is_active = true
WHERE name IN ('Free Trial (14-Day)', 'Commercial Starter (Monthly)', 'Enterprise Scale (Annual)');

