-- ============================================================
-- V4 - Seed sample users
-- ============================================================

INSERT INTO users (
    id,
    username,
    email,
    password_hash,
    first_name,
    last_name,
    status,
    enabled,
    created_at,
    updated_at
)
VALUES
(
    gen_random_uuid(),
    'customer1',
    'customer1@example.com',
    '$2a$10$REPLACE_WITH_BCRYPT_HASH',
    'John',
    'Customer',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'vendor1',
    'vendor1@example.com',
    '$2a$10$REPLACE_WITH_BCRYPT_HASH',
    'Jane',
    'Vendor',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'admin1',
    'admin1@example.com',
    '$2a$10$REPLACE_WITH_BCRYPT_HASH',
    'System',
    'Administrator',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    gen_random_uuid(),
    'superadmin1',
    'superadmin1@example.com',
    '$2a$10$REPLACE_WITH_BCRYPT_HASH',
    'System',
    'SuperAdmin',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (username) DO NOTHING;


-- ============================================================
-- Assign CUSTOMER role
-- ============================================================

INSERT INTO user_roles (
    user_id,
    role_id
)
SELECT
    u.id,
    r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'customer1'
  AND r.name = 'CUSTOMER'
ON CONFLICT DO NOTHING;


-- ============================================================
-- Assign VENDOR role
-- ============================================================

INSERT INTO user_roles (
    user_id,
    role_id
)
SELECT
    u.id,
    r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'vendor1'
  AND r.name = 'VENDOR'
ON CONFLICT DO NOTHING;


-- ============================================================
-- Assign ADMIN role
-- ============================================================

INSERT INTO user_roles (
    user_id,
    role_id
)
SELECT
    u.id,
    r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'admin1'
  AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;


-- ============================================================
-- Assign SUPER_ADMIN role
-- ============================================================

INSERT INTO user_roles (
    user_id,
    role_id
)
SELECT
    u.id,
    r.id
FROM users u
CROSS JOIN roles r
WHERE u.username = 'superadmin1'
  AND r.name = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;