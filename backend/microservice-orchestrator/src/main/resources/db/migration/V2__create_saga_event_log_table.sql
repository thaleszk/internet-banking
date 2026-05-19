CREATE TABLE saga_event_log (
    id BIGSERIAL PRIMARY KEY,

    saga_id VARCHAR(100) NOT NULL,
    saga_type VARCHAR(100) NOT NULL,

    step VARCHAR(100),
    status VARCHAR(100),

    message_type VARCHAR(100),
    routing_key VARCHAR(255),

    payload JSONB,

    error_message TEXT,

    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_saga_event_log_saga_instance
        FOREIGN KEY (saga_id)
        REFERENCES saga_instance (saga_id)
);

CREATE INDEX idx_saga_event_log_saga_id
    ON saga_event_log (saga_id);

CREATE INDEX idx_saga_event_log_saga_type
    ON saga_event_log (saga_type);

CREATE INDEX idx_saga_event_log_step
    ON saga_event_log (step);

CREATE INDEX idx_saga_event_log_status
    ON saga_event_log (status);

CREATE INDEX idx_saga_event_log_created_at
    ON saga_event_log (created_at);