INSERT INTO permissions (
    id,
    name,
    description,
    created_at,
    updated_at
)
VALUES
    (
        gen_random_uuid(),
        'USER_READ',
        'Read users',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'USER_CREATE',
        'Create users',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'USER_UPDATE',
        'Update users',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'USER_DELETE',
        'Delete users',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'ROLE_READ',
        'Read roles',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'ROLE_CREATE',
        'Create roles',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'ROLE_UPDATE',
        'Update roles',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'ROLE_DELETE',
        'Delete roles',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (name) DO NOTHING;


INSERT INTO roles (
    id,
    name,
    description,
    created_at,
    updated_at
)
VALUES
    (
        gen_random_uuid(),
        'CUSTOMER',
        'Customer role',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'VENDOR',
        'Vendor role',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'ADMIN',
        'Administrator role',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    ),
    (
        gen_random_uuid(),
        'SUPER_ADMIN',
        'Super administrator role',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (name) DO NOTHING;


INSERT INTO role_permissions (
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
  AND p.name IN (
      'USER_READ',
      'USER_CREATE',
      'USER_UPDATE',
      'USER_DELETE',
      'ROLE_READ',
      'ROLE_CREATE',
      'ROLE_UPDATE',
      'ROLE_DELETE'
  )
ON CONFLICT DO NOTHING;


INSERT INTO role_permissions (
    role_id,
    permission_id
)
SELECT
    r.id,
    p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;