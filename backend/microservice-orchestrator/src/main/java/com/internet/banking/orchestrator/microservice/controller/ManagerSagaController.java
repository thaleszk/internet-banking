package com.internet.banking.orchestrator.microservice.controller;

import com.internet.banking.orchestrator.microservice.event.DeleteManagerEvent;
import com.internet.banking.orchestrator.microservice.service.ManagerSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manager-saga")
@RequiredArgsConstructor
public class ManagerSagaController {

    private final ManagerSagaService service;

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteManager(
            @PathVariable String cpf
    ) {
        DeleteManagerEvent event = new DeleteManagerEvent(cpf);
        service.start(event);

        return ResponseEntity.accepted().build();
    }
}