CREATE TABLE saga_instance (
    id BIGSERIAL PRIMARY KEY,

    saga_id VARCHAR(100) NOT NULL,
    saga_type VARCHAR(100) NOT NULL,

    current_status VARCHAR(100) NOT NULL,
    current_step VARCHAR(100),

    correlation_key VARCHAR(255),

    payload JSONB,

    error_message TEXT,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    CONSTRAINT uk_saga_instance_saga_id UNIQUE (saga_id)
);

CREATE INDEX idx_saga_instance_saga_type
    ON saga_instance (saga_type);

CREATE INDEX idx_saga_instance_current_status
    ON saga_instance (current_status);

CREATE INDEX idx_saga_instance_correlation_key
    ON saga_instance (correlation_key);

CREATE INDEX idx_saga_instance_created_at
    ON saga_instance (created_at);