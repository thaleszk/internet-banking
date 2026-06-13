package com.internet.banking.orchestrator.microservice.command;

import java.time.LocalDateTime;

public record CreateCustomerForSelfRegistrationCommand(
        String sagaId,
        String sagaType,
        String correlationId,
        LocalDateTime occurredAt,
        String name,
        String cpf,
        String email,
        String phone
) {
}
