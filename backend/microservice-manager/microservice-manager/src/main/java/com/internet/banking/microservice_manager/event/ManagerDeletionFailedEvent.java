package com.internet.banking.microservice_manager.event;

import java.io.Serializable;

public record ManagerDeletionFailedEvent(

        String sagaId,

        String errorMessage

)implements Serializable {
}