package com.internet.banking.microservice.account.event;

import java.io.Serializable;

public record AccountsTransferFailedEvent(

        String sagaId,

        String errorMessage

) implements Serializable {
}