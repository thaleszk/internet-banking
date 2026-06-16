package com.internet.banking.microservice_manager.command;

import java.time.LocalDateTime;

public record FindReplacementManagerCommand(

        String sagaId,

        String sagaType,

        String correlationKey,

        LocalDateTime createdAt,

        Long managerId,

        String managerCpf

) {
}