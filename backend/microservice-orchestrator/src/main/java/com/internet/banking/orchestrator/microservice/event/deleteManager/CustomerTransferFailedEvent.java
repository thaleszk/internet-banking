package com.internet.banking.orchestrator.microservice.event.deleteManager;


public record CustomerTransferFailedEvent(

        String sagaId,

        String errorMessage

) {
}