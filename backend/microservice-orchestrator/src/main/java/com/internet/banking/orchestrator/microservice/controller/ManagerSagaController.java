package com.internet.banking.orchestrator.microservice.controller;

import com.internet.banking.orchestrator.microservice.dto.DeleteManagerRequest;
import com.internet.banking.orchestrator.microservice.dto.DeleteManagerResponse;
import com.internet.banking.orchestrator.microservice.enums.DeleteManagerSagaStatus;
import com.internet.banking.orchestrator.microservice.handler.DeleteManagerSagaHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/manager-saga")
public class ManagerSagaController {

    private final DeleteManagerSagaHandler sagaHandler;

    public ManagerSagaController(final DeleteManagerSagaHandler sagaHandler) {
        this.sagaHandler = sagaHandler;
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<DeleteManagerResponse> deleteManager(
            @PathVariable String cpf
    ) {
        DeleteManagerRequest request = new DeleteManagerRequest(cpf);
        final String sagaId = sagaHandler.start(request);

        final DeleteManagerResponse response =
                new DeleteManagerResponse(
                        request.cpf(),
                        sagaId,
                        DeleteManagerSagaStatus.STARTED.name(),
                        "Delete manager saga started"
                );

        return ResponseEntity.accepted().body(response);
    }
}