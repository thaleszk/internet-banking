package com.internet.banking.microservice_manager.command;

import java.time.LocalDateTime;

public record DeleteManagerCommand(

        String sagaId,

        String sagaType,

        String correlationKey,

        LocalDateTime createdAt,

        String managerCpf,
        String replacementManagerCpf


) {
}