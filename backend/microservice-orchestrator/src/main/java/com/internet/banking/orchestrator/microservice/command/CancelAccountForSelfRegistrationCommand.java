package com.internet.banking.orchestrator.microservice.command;

import java.time.LocalDateTime;

public record CancelAccountForSelfRegistrationCommand(
        String sagaId,
        String sagaType,
        String correlationId,
        LocalDateTime occurredAt,
        Long accountId,
        String reason
) {
}
