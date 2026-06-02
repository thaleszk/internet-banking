package com.internet.banking.orchestrator.microservice.controller;

import com.internet.banking.orchestrator.microservice.dto.SelfRegisterRequestDTO;
import com.internet.banking.orchestrator.microservice.service.ManagerSagaService;
import com.internet.banking.orchestrator.microservice.service.SelfRegisterSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sagas")
@RequiredArgsConstructor
public class SelfRegisterSagaController {

    private final SelfRegisterSagaService service;

    @PostMapping("/self-register")
    public ResponseEntity<Void> selfRegister(
            @RequestBody SelfRegisterRequestDTO dto
    ) {
        service.startSaga(dto);

        return ResponseEntity.accepted().build();
    }
}