package com.internet.banking.orchestrator.microservice.command;

import java.time.LocalDateTime;

public record DeactivateAuthenticationForSelfRegistrationCommand(
        String sagaId,
        String sagaType,
        String correlationId,
        LocalDateTime occurredAt,
        Long authenticationId,
        String reason
) {
}
