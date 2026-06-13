package com.internet.banking.orchestrator.microservice.event;

import java.time.LocalDateTime;

public record CustomerDeactivatedForSelfRegistrationEvent(
        String sagaId,
        String sagaType,
        String correlationId,
        LocalDateTime occurredAt,
        Long customerId
) {
}
