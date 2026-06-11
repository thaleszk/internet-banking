package com.internet.banking.microservice.account.command;

import java.time.LocalDateTime;

public record TransferAccountsCommand(

        String sagaId,

        String sagaType,

        String correlationKey,

        LocalDateTime createdAt,

        String currentManagerCpf,

        String replacementManagerCpf

) {
}