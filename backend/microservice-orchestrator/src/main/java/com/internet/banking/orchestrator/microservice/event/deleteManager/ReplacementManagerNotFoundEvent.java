package com.internet.banking.orchestrator.microservice.event.deleteManager;

public record ReplacementManagerNotFoundEvent(

        String sagaId,

        String errorMessage

) {
}