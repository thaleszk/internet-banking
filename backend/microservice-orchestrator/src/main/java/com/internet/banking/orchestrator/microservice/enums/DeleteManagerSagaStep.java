package com.internet.banking.orchestrator.microservice.enums;


public enum DeleteManagerSagaStep {

    FIND_REPLACEMENT_MANAGER,

    TRANSFER_CUSTOMERS,

    DELETE_MANAGER,

    COMPLETE

}