package com.internet.banking.orchestrator.microservice.controller;

import com.internet.banking.orchestrator.microservice.dto.CustomerSelfRegistrationRequest;
import com.internet.banking.orchestrator.microservice.dto.CustomerSelfRegistrationResponse;
import com.internet.banking.orchestrator.microservice.handler.CustomerSelfRegistrationSagaHandler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/customer-self-registration")
public class CustomerSelfRegistrationController {

    private final CustomerSelfRegistrationSagaHandler sagaHandler;

    public CustomerSelfRegistrationController(
            final CustomerSelfRegistrationSagaHandler sagaHandler
    ) {
        this.sagaHandler = sagaHandler;
    }

    @PostMapping
    public ResponseEntity<CustomerSelfRegistrationResponse> start(
            @RequestBody @Valid final CustomerSelfRegistrationRequest request
    ) {
        final String sagaId = sagaHandler.start(request);

        final CustomerSelfRegistrationResponse response =
                new CustomerSelfRegistrationResponse(
                        request.cpf(),
                        request.email(),
                        sagaId,
                        "STARTED",
                        "Customer self registration saga started"
                );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("erro", exception.getMessage()));
    }
}
