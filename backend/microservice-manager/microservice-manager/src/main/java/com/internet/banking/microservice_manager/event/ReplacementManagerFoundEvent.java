package com.internet.banking.microservice_manager.event;

public record ReplacementManagerFoundEvent(

        String sagaId,

        Long managerId,

        Long replacementManagerId

) {
}