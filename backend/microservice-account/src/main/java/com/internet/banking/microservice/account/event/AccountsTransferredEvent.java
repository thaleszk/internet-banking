package com.internet.banking.microservice.account.event;

import java.io.Serializable;

public record AccountsTransferredEvent(

        String sagaId,

        Integer transferredAccounts

) implements Serializable {
}