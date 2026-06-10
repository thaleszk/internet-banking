package com.internet.banking.orchestrator.microservice.event.deleteManager;

public record CustomersTransferredEvent(

        String sagaId,

        Integer transferredCustomers

) {
}