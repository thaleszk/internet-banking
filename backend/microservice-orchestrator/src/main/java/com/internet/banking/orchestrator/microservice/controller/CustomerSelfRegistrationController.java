package com.internet.banking.orchestrator.microservice.controller;

import com.internet.banking.orchestrator.microservice.dto.CustomerSelfRegistrationRequest;
import com.internet.banking.orchestrator.microservice.dto.CustomerSelfRegistrationResponse;
import com.internet.banking.orchestrator.microservice.handler.CustomerSelfRegistrationSagaHandler;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                        sagaId,
                        "STARTED",
                        "Customer self registration saga started"
                );

        return ResponseEntity.accepted().body(response);
    }
}
