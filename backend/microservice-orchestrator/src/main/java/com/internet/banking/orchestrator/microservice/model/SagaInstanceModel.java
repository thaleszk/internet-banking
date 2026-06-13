package com.internet.banking.orchestrator.microservice.model;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "saga_instance")
@Data
public class SagaInstanceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saga_id", nullable = false, unique = true, length = 100)
    private String sagaId;

    @Column(name = "saga_type", nullable = false, length = 100)
    private String sagaType;

    @Column(name = "current_status", nullable = false, length = 100)
    private String currentStatus;

    @Column(name = "current_step", length = 100)
    private String currentStep;

    @Column(name = "correlation_key")
    private String correlationKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", columnDefinition = "jsonb")
    private String payload;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
