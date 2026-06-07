package com.internet.banking.orchestrator.microservice.command;

import java.time.LocalDateTime;

public record CreateAccountForSelfRegistrationCommand(
        String sagaId,
        String sagaType,
        String correlationId,
        LocalDateTime occurredAt,
        Long customerId,
        Long managerId
) {
}
