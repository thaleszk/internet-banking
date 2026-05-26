package com.internet.banking.orchestrator.microservice.dto;

public record CustomerSelfRegistrationResponse(
        String sagaId,
        String status,
        String message
) {
}
