package com.internet.banking.orchestrator.microservice.config;

public final class CustomerSelfRegistrationRabbitConstants {

    private CustomerSelfRegistrationRabbitConstants() {
    }

    public static final String SAGA_COMMAND_EXCHANGE = "saga.command.exchange";
    public static final String SAGA_EVENT_EXCHANGE = "saga.event.exchange";

    // Commands routing keys
    public static final String CREATE_CUSTOMER_COMMAND_ROUTING_KEY =
            "saga.customer-self-registration.customer.create.command";

    public static final String ASSIGN_MANAGER_COMMAND_ROUTING_KEY =
            "saga.customer-self-registration.manager.assign.command";

    public static final String CREATE_ACCOUNT_COMMAND_ROUTING_KEY =
            "saga.customer-self-registration.account.create.command";

    public static final String CREATE_AUTHENTICATION_COMMAND_ROUTING_KEY =
            "saga.customer-self-registration.authentication.create.command";

    public static final String CANCEL_ACCOUNT_COMMAND_ROUTING_KEY =
            "saga.customer-self-registration.account.cancel.command";

    public static final String DEACTIVATE_CUSTOMER_COMMAND_ROUTING_KEY =
            "saga.customer-self-registration.customer.deactivate.command";

    public static final String DEACTIVATE_AUTHENTICATION_COMMAND_ROUTING_KEY =
            "saga.customer-self-registration.authentication.deactivate.command";

    // Event routing keys
    public static final String CUSTOMER_CREATED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.customer.created.event";

    public static final String CUSTOMER_CREATION_FAILED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.customer.creation-failed.event";

    public static final String MANAGER_ASSIGNED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.manager.assigned.event";

    public static final String MANAGER_ASSIGNMENT_FAILED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.manager.assignment-failed.event";

    public static final String ACCOUNT_CREATED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.account.created.event";

    public static final String ACCOUNT_CREATION_FAILED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.account.creation-failed.event";

    public static final String AUTHENTICATION_CREATED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.authentication.created.event";

    public static final String AUTHENTICATION_CREATION_FAILED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.authentication.creation-failed.event";

    public static final String ACCOUNT_CANCELLED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.account.cancelled.event";

    public static final String ACCOUNT_CANCELLATION_FAILED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.account.cancellation-failed.event";

    public static final String CUSTOMER_DEACTIVATED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.customer.deactivated.event";

    public static final String CUSTOMER_DEACTIVATION_FAILED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.customer.deactivation-failed.event";

    public static final String AUTHENTICATION_DEACTIVATED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.authentication.deactivated.event";

    public static final String AUTHENTICATION_DEACTIVATION_FAILED_EVENT_ROUTING_KEY =
            "saga.customer-self-registration.authentication.deactivation-failed.event";

    // Command queues consumed by domain microservices
    public static final String CREATE_CUSTOMER_COMMAND_QUEUE =
            "customer-service.customer-self-registration.create-customer.queue";

    public static final String ASSIGN_MANAGER_COMMAND_QUEUE =
            "manager-service.customer-self-registration.assign-manager.queue";

    public static final String CREATE_ACCOUNT_COMMAND_QUEUE =
            "account-service.customer-self-registration.create-account.queue";

    public static final String CREATE_AUTHENTICATION_COMMAND_QUEUE =
            "authentication-service.customer-self-registration.create-authentication.queue";

    public static final String CANCEL_ACCOUNT_COMMAND_QUEUE =
            "account-service.customer-self-registration.cancel-account.queue";

    public static final String DEACTIVATE_CUSTOMER_COMMAND_QUEUE =
            "customer-service.customer-self-registration.deactivate-customer.queue";

    public static final String DEACTIVATE_AUTHENTICATION_COMMAND_QUEUE =
            "authentication-service.customer-self-registration.deactivate-authentication.queue";

    // Event queues consumed by saga orchestrator
    public static final String CUSTOMER_CREATED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.customer-created.queue";

    public static final String CUSTOMER_CREATION_FAILED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.customer-creation-failed.queue";

    public static final String MANAGER_ASSIGNED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.manager-assigned.queue";

    public static final String MANAGER_ASSIGNMENT_FAILED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.manager-assignment-failed.queue";

    public static final String ACCOUNT_CREATED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.account-created.queue";

    public static final String ACCOUNT_CREATION_FAILED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.account-creation-failed.queue";

    public static final String AUTHENTICATION_CREATED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.authentication-created.queue";

    public static final String AUTHENTICATION_CREATION_FAILED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.authentication-creation-failed.queue";

    public static final String ACCOUNT_CANCELLED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.account-cancelled.queue";

    public static final String ACCOUNT_CANCELLATION_FAILED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.account-cancellation-failed.queue";

    public static final String CUSTOMER_DEACTIVATED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.customer-deactivated.queue";

    public static final String CUSTOMER_DEACTIVATION_FAILED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.customer-deactivation-failed.queue";

    public static final String AUTHENTICATION_DEACTIVATED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.authentication-deactivated.queue";

    public static final String AUTHENTICATION_DEACTIVATION_FAILED_EVENT_QUEUE =
            "saga-orchestrator.customer-self-registration.authentication-deactivation-failed.queue";
}
