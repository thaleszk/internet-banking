package com.internet.banking.orchestrator.microservice.event.deleteManager;


public record AccountsTransferFailedEvent(

        String sagaId,

        String errorMessage

) {
}