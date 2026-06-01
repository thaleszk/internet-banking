package com.internet.banking.orchestrator.microservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.internet.banking.orchestrator.microservice.config.CustomerSelfRegistrationRabbitConstants.*;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE =
            "manager.exchange";

    public static final String DELETE_QUEUE =
            "manager.delete.requested.queue";

    public static final String ROUTING_KEY =
            "manager.delete.requested";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(DELETE_QUEUE);
    }

    @Bean
    public Binding binding(
            Queue deleteQueue,
            DirectExchange exchange
    ) {

        return BindingBuilder
                .bind(deleteQueue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Declarables customerSelfRegistrationTopology() {
        DirectExchange commandExchange = new DirectExchange(SAGA_COMMAND_EXCHANGE);
        DirectExchange eventExchange = new DirectExchange(SAGA_EVENT_EXCHANGE);

        Queue createCustomerCommandQueue = new Queue(CREATE_CUSTOMER_COMMAND_QUEUE);
        Queue assignManagerCommandQueue = new Queue(ASSIGN_MANAGER_COMMAND_QUEUE);
        Queue createAccountCommandQueue = new Queue(CREATE_ACCOUNT_COMMAND_QUEUE);
        Queue createAuthenticationCommandQueue = new Queue(CREATE_AUTHENTICATION_COMMAND_QUEUE);
        Queue cancelAccountCommandQueue = new Queue(CANCEL_ACCOUNT_COMMAND_QUEUE);
        Queue deactivateCustomerCommandQueue = new Queue(DEACTIVATE_CUSTOMER_COMMAND_QUEUE);
        Queue deactivateAuthenticationCommandQueue = new Queue(DEACTIVATE_AUTHENTICATION_COMMAND_QUEUE);

        Queue customerCreatedEventQueue = new Queue(CUSTOMER_CREATED_EVENT_QUEUE);
        Queue customerCreationFailedEventQueue = new Queue(CUSTOMER_CREATION_FAILED_EVENT_QUEUE);
        Queue managerAssignedEventQueue = new Queue(MANAGER_ASSIGNED_EVENT_QUEUE);
        Queue managerAssignmentFailedEventQueue = new Queue(MANAGER_ASSIGNMENT_FAILED_EVENT_QUEUE);
        Queue accountCreatedEventQueue = new Queue(ACCOUNT_CREATED_EVENT_QUEUE);
        Queue accountCreationFailedEventQueue = new Queue(ACCOUNT_CREATION_FAILED_EVENT_QUEUE);
        Queue authenticationCreatedEventQueue = new Queue(AUTHENTICATION_CREATED_EVENT_QUEUE);
        Queue authenticationCreationFailedEventQueue = new Queue(AUTHENTICATION_CREATION_FAILED_EVENT_QUEUE);
        Queue accountCancelledEventQueue = new Queue(ACCOUNT_CANCELLED_EVENT_QUEUE);
        Queue accountCancellationFailedEventQueue = new Queue(ACCOUNT_CANCELLATION_FAILED_EVENT_QUEUE);
        Queue customerDeactivatedEventQueue = new Queue(CUSTOMER_DEACTIVATED_EVENT_QUEUE);
        Queue customerDeactivationFailedEventQueue = new Queue(CUSTOMER_DEACTIVATION_FAILED_EVENT_QUEUE);
        Queue authenticationDeactivatedEventQueue = new Queue(AUTHENTICATION_DEACTIVATED_EVENT_QUEUE);
        Queue authenticationDeactivationFailedEventQueue = new Queue(AUTHENTICATION_DEACTIVATION_FAILED_EVENT_QUEUE);

        return new Declarables(
                commandExchange,
                eventExchange,
                createCustomerCommandQueue,
                assignManagerCommandQueue,
                createAccountCommandQueue,
                createAuthenticationCommandQueue,
                cancelAccountCommandQueue,
                deactivateCustomerCommandQueue,
                deactivateAuthenticationCommandQueue,
                customerCreatedEventQueue,
                customerCreationFailedEventQueue,
                managerAssignedEventQueue,
                managerAssignmentFailedEventQueue,
                accountCreatedEventQueue,
                accountCreationFailedEventQueue,
                authenticationCreatedEventQueue,
                authenticationCreationFailedEventQueue,
                accountCancelledEventQueue,
                accountCancellationFailedEventQueue,
                customerDeactivatedEventQueue,
                customerDeactivationFailedEventQueue,
                authenticationDeactivatedEventQueue,
                authenticationDeactivationFailedEventQueue,
                BindingBuilder.bind(createCustomerCommandQueue).to(commandExchange).with(CREATE_CUSTOMER_COMMAND_ROUTING_KEY),
                BindingBuilder.bind(assignManagerCommandQueue).to(commandExchange).with(ASSIGN_MANAGER_COMMAND_ROUTING_KEY),
                BindingBuilder.bind(createAccountCommandQueue).to(commandExchange).with(CREATE_ACCOUNT_COMMAND_ROUTING_KEY),
                BindingBuilder.bind(createAuthenticationCommandQueue).to(commandExchange).with(CREATE_AUTHENTICATION_COMMAND_ROUTING_KEY),
                BindingBuilder.bind(cancelAccountCommandQueue).to(commandExchange).with(CANCEL_ACCOUNT_COMMAND_ROUTING_KEY),
                BindingBuilder.bind(deactivateCustomerCommandQueue).to(commandExchange).with(DEACTIVATE_CUSTOMER_COMMAND_ROUTING_KEY),
                BindingBuilder.bind(deactivateAuthenticationCommandQueue).to(commandExchange).with(DEACTIVATE_AUTHENTICATION_COMMAND_ROUTING_KEY),
                BindingBuilder.bind(customerCreatedEventQueue).to(eventExchange).with(CUSTOMER_CREATED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(customerCreationFailedEventQueue).to(eventExchange).with(CUSTOMER_CREATION_FAILED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(managerAssignedEventQueue).to(eventExchange).with(MANAGER_ASSIGNED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(managerAssignmentFailedEventQueue).to(eventExchange).with(MANAGER_ASSIGNMENT_FAILED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(accountCreatedEventQueue).to(eventExchange).with(ACCOUNT_CREATED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(accountCreationFailedEventQueue).to(eventExchange).with(ACCOUNT_CREATION_FAILED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(authenticationCreatedEventQueue).to(eventExchange).with(AUTHENTICATION_CREATED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(authenticationCreationFailedEventQueue).to(eventExchange).with(AUTHENTICATION_CREATION_FAILED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(accountCancelledEventQueue).to(eventExchange).with(ACCOUNT_CANCELLED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(accountCancellationFailedEventQueue).to(eventExchange).with(ACCOUNT_CANCELLATION_FAILED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(customerDeactivatedEventQueue).to(eventExchange).with(CUSTOMER_DEACTIVATED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(customerDeactivationFailedEventQueue).to(eventExchange).with(CUSTOMER_DEACTIVATION_FAILED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(authenticationDeactivatedEventQueue).to(eventExchange).with(AUTHENTICATION_DEACTIVATED_EVENT_ROUTING_KEY),
                BindingBuilder.bind(authenticationDeactivationFailedEventQueue).to(eventExchange).with(AUTHENTICATION_DEACTIVATION_FAILED_EVENT_ROUTING_KEY)
        );
    }
}
