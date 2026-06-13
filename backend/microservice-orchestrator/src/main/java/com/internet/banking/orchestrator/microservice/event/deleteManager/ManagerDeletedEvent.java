package com.internet.banking.orchestrator.microservice.event.deleteManager;

public record ManagerDeletedEvent(
        String sagaId,
        Long managerId
) {
}