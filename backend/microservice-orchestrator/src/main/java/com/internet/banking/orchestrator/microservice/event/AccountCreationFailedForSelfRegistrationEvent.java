package com.internet.banking.orchestrator.microservice.event;

import java.time.LocalDateTime;

public record AccountCreationFailedForSelfRegistrationEvent(
        String sagaId,
        String sagaType,
        String correlationId,
        LocalDateTime occurredAt,
        String errorMessage
) {
}
