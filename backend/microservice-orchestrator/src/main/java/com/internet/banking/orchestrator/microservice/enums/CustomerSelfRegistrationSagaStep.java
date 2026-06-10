package com.internet.banking.orchestrator.microservice.enums;

public enum CustomerSelfRegistrationSagaStep {
    CREATE_CUSTOMER,
    ASSIGN_ACCOUNT_MANAGER,
    CREATE_ACCOUNT,
    CREATE_AUTHENTICATION,
    COMPLETE_REGISTRATION,

    CANCEL_ACCOUNT,
    DEACTIVATE_CUSTOMER,
    DEACTIVATE_AUTHENTICATION
}



