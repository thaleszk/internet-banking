package com.internet.banking.orchestrator.microservice.command.deleteManager;

import java.time.LocalDateTime;

public record FindReplacementManagerCommand(

        String sagaId,

        String sagaType,

        String correlationKey,

        LocalDateTime createdAt,

        String managerCpf

) {

}