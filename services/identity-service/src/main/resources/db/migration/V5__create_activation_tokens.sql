CREATE TABLE activation_tokens (
    id UUID PRIMARY KEY,

    token VARCHAR(255) NOT NULL,

    user_id UUID NOT NULL,

    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,

    used BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT uk_activation_tokens_token
        UNIQUE (token),

    CONSTRAINT fk_activation_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE INDEX idx_activation_tokens_token
    ON activation_tokens(token);

CREATE INDEX idx_activation_tokens_user_id
    ON activation_tokens(user_id);