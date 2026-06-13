package com.internet.banking.orchestrator.microservice.event.deleteManager;

public record ManagerDeletionFailedEvent(

        String sagaId,

        String errorMessage

) {
}