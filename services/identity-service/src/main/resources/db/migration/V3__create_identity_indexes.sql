CREATE INDEX idx_users_status
    ON users(status);

CREATE INDEX idx_users_enabled
    ON users(enabled);

CREATE INDEX idx_user_roles_role_id
    ON user_roles(role_id);

CREATE INDEX idx_role_permissions_permission_id
    ON role_permissions(permission_id);