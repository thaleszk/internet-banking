package com.internet.banking.microservice_manager.event;

import java.io.Serializable;

public record ReplacementManagerFoundEvent(

        String sagaId,

        Long managerId,

        Long replacementManagerId,

        String replacementManagerCpf

)implements Serializable {
}