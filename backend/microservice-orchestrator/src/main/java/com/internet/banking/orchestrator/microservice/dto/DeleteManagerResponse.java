package com.internet.banking.orchestrator.microservice.dto;

public record DeleteManagerResponse(
        String cpf,
        String sagaId,
        String status,
        String message
) {
}
