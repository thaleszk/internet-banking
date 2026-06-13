package com.internet.banking.microservice_manager.event;

import java.io.Serializable;

public record ReplacementManagerNotFoundEvent(

        String sagaId,

        String errorMessage

) implements Serializable {
}