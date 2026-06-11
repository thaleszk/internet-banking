package com.internet.banking.orchestrator.microservice.event.deleteManager;

public record ReplacementManagerFoundEvent(

        String sagaId,

        Long managerId,

        Long replacementManagerId,

        String replacementManagerCpf

) {
}