package com.internet.banking.orchestrator.microservice.enums;

public enum DeleteManagerSagaStatus {

    STARTED,

    REPLACEMENT_MANAGER_REQUESTED,

    REPLACEMENT_MANAGER_FOUND,

    CUSTOMER_TRANSFER_REQUESTED,

    CUSTOMERS_TRANSFERRED,

    MANAGER_DELETION_REQUESTED,

    COMPLETED,

    COMPENSATION_STARTED,

    FAILED

}