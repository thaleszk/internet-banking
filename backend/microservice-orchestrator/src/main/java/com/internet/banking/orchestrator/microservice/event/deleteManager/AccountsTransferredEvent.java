package com.internet.banking.orchestrator.microservice.event.deleteManager;

public record AccountsTransferredEvent(

        String sagaId,

        Integer transferredAccounts

) {
}