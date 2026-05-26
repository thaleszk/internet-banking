package com.internet.banking.orchestrator.microservice.event;

import com.internet.banking.orchestrator.microservice.config.CustomerSelfRegistrationRabbitConstants;
import com.internet.banking.orchestrator.microservice.handler.CustomerSelfRegistrationSagaHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerSelfRegistrationSagaListener {

    private final CustomerSelfRegistrationSagaHandler sagaHandler;

    public CustomerSelfRegistrationSagaListener(
            final CustomerSelfRegistrationSagaHandler sagaHandler
    ) {
        this.sagaHandler = sagaHandler;
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.CUSTOMER_CREATED_EVENT_QUEUE)
    public void onCustomerCreated(final CustomerCreatedForSelfRegistrationEvent event) {
        sagaHandler.handleCustomerCreated(event);
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.CUSTOMER_CREATION_FAILED_EVENT_QUEUE)
    public void onCustomerCreationFailed(final CustomerCreationFailedForSelfRegistrationEvent event) {
        sagaHandler.handleCustomerCreationFailed(event);
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.MANAGER_ASSIGNED_EVENT_QUEUE)
    public void onManagerAssigned(final ManagerAssignedForSelfRegistrationEvent event) {
        sagaHandler.handleManagerAssigned(event);
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.MANAGER_ASSIGNMENT_FAILED_EVENT_QUEUE)
    public void onManagerAssignmentFailed(final ManagerAssignmentFailedForSelfRegistrationEvent event) {
        sagaHandler.handleManagerAssignmentFailed(event);
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.ACCOUNT_CREATED_EVENT_QUEUE)
    public void onAccountCreated(final AccountCreatedForSelfRegistrationEvent event) {
        sagaHandler.handleAccountCreated(event);
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.ACCOUNT_CREATION_FAILED_EVENT_QUEUE)
    public void onAccountCreationFailed(final AccountCreationFailedForSelfRegistrationEvent event) {
        sagaHandler.handleAccountCreationFailed(event);
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.AUTHENTICATION_CREATED_EVENT_QUEUE)
    public void onAuthenticationCreated(final AuthenticationCreatedForSelfRegistrationEvent event) {
        sagaHandler.handleAuthenticationCreated(event);
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.AUTHENTICATION_CREATION_FAILED_EVENT_QUEUE)
    public void onAuthenticationCreationFailed(final AuthenticationCreationFailedForSelfRegistrationEvent event) {
        sagaHandler.handleAuthenticationCreationFailed(event);
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.ACCOUNT_CANCELLED_EVENT_QUEUE)
    public void onAccountCancelled(final AccountCancelledForSelfRegistrationEvent event) {
        sagaHandler.handleAccountCancelled(event);
    }

    @RabbitListener(queues = CustomerSelfRegistrationRabbitConstants.CUSTOMER_DEACTIVATED_EVENT_QUEUE)
    public void onCustomerDeactivated(final CustomerDeactivatedForSelfRegistrationEvent event) {
        sagaHandler.handleCustomerDeactivated(event);
    }
}
