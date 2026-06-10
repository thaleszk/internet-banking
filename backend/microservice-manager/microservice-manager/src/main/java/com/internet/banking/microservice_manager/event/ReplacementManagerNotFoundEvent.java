package com.internet.banking.microservice_manager.event;

public record ReplacementManagerNotFoundEvent(

        String sagaId,

        String errorMessage

) {
}