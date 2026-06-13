package com.internet.banking.orchestrator.microservice.service;

import com.internet.banking.orchestrator.microservice.dto.CustomerSelfRegistrationRequest;
import com.internet.banking.orchestrator.microservice.handler.CustomerSelfRegistrationSagaHandler;
import org.springframework.stereotype.Service;

@Service
public class SagaOrchestratorService {

    private final CustomerSelfRegistrationSagaHandler customerSelfRegistrationSagaHandler;

    public SagaOrchestratorService(
            final CustomerSelfRegistrationSagaHandler customerSelfRegistrationSagaHandler
    ) {
        this.customerSelfRegistrationSagaHandler = customerSelfRegistrationSagaHandler;
    }

    public void startCustomerSelfRegistration(final CustomerSelfRegistrationRequest request) {
        customerSelfRegistrationSagaHandler.start(request);
    }
}
