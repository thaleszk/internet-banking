package com.internet.banking.orchestrator.microservice.service;

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
