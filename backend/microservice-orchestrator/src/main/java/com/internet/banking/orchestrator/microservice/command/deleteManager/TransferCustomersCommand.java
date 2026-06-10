package com.internet.banking.orchestrator.microservice.command.deleteManager;

import java.time.LocalDateTime;

public record TransferCustomersCommand(

        String sagaId,

        String sagaType,

        String correlationKey,

        LocalDateTime createdAt,

        Long managerId,

        Long replacementManagerId

) {
}