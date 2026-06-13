package com.internet.banking.orchestrator.microservice.event;

import java.time.LocalDateTime;

public record CustomerCreatedForSelfRegistrationEvent(
        String sagaId,
        String sagaType,
        String correlationId,
        LocalDateTime occurredAt,
        Long customerId,
        String cpf,
        String email
) {
}
