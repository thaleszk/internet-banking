package com.internet.banking.microservice_manager.event;

import java.io.Serializable;

public record ManagerDeletedEvent(

        String sagaId

) implements Serializable {
}