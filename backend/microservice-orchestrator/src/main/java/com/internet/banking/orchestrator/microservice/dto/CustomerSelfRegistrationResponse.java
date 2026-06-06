package com.internet.banking.orchestrator.microservice.dto;

public record CustomerSelfRegistrationResponse(
        String cpf,
        String email,
        String sagaId,
        String status,
        String message
) {
}
