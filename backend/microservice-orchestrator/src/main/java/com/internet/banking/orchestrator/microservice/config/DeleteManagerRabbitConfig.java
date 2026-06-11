package com.internet.banking.orchestrator.microservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.internet.banking.orchestrator.microservice.config.DeleteManagerRabbitConstants.*;

@Configuration
public class DeleteManagerRabbitConfig {

    @Bean
    public Declarables deleteManagerTopology() {

        DirectExchange commandExchange =
                new DirectExchange(SAGA_COMMAND_EXCHANGE);

        DirectExchange eventExchange =
                new DirectExchange(SAGA_EVENT_EXCHANGE);

        Queue findReplacementManagerCommandQueue =
                new Queue(FIND_REPLACEMENT_MANAGER_COMMAND_QUEUE);

        Queue transferAccountsCommandQueue =
                new Queue(TRANSFER_ACCOUNTS_COMMAND_QUEUE);

        Queue deleteManagerCommandQueue =
                new Queue(DELETE_MANAGER_COMMAND_QUEUE);

        Queue replacementManagerFoundEventQueue =
                new Queue(REPLACEMENT_MANAGER_FOUND_EVENT_QUEUE);

        Queue replacementManagerNotFoundEventQueue =
                new Queue(REPLACEMENT_MANAGER_NOT_FOUND_EVENT_QUEUE);

        Queue accountsTransferredEventQueue =
                new Queue(ACCOUNTS_TRANSFERRED_EVENT_QUEUE);

        Queue accountsTransferFailedEventQueue =
                new Queue(ACCOUNTS_TRANSFER_FAILED_EVENT_QUEUE);

        Queue managerDeletedEventQueue =
                new Queue(MANAGER_DELETED_EVENT_QUEUE);

        Queue managerDeletionFailedEventQueue =
                new Queue(MANAGER_DELETION_FAILED_EVENT_QUEUE);

        return new Declarables(

                commandExchange,
                eventExchange,

                findReplacementManagerCommandQueue,
                transferAccountsCommandQueue,
                deleteManagerCommandQueue,

                replacementManagerFoundEventQueue,
                replacementManagerNotFoundEventQueue,
                accountsTransferredEventQueue,
                accountsTransferFailedEventQueue,
                managerDeletedEventQueue,
                managerDeletionFailedEventQueue,

                BindingBuilder.bind(findReplacementManagerCommandQueue)
                        .to(commandExchange)
                        .with(FIND_REPLACEMENT_MANAGER_COMMAND_ROUTING_KEY),

                BindingBuilder.bind(transferAccountsCommandQueue)
                        .to(commandExchange)
                        .with(TRANSFER_ACCOUNTS_COMMAND_ROUTING_KEY),

                BindingBuilder.bind(deleteManagerCommandQueue)
                        .to(commandExchange)
                        .with(DELETE_MANAGER_COMMAND_ROUTING_KEY),

                BindingBuilder.bind(replacementManagerFoundEventQueue)
                        .to(eventExchange)
                        .with(REPLACEMENT_MANAGER_FOUND_EVENT_ROUTING_KEY),

                BindingBuilder.bind(replacementManagerNotFoundEventQueue)
                        .to(eventExchange)
                        .with(REPLACEMENT_MANAGER_NOT_FOUND_EVENT_ROUTING_KEY),

                BindingBuilder.bind(accountsTransferredEventQueue)
                        .to(eventExchange)
                        .with(ACCOUNTS_TRANSFERRED_EVENT_ROUTING_KEY),

                BindingBuilder.bind(accountsTransferFailedEventQueue)
                        .to(eventExchange)
                        .with(ACCOUNTS_TRANSFER_FAILED_EVENT_ROUTING_KEY),

                BindingBuilder.bind(managerDeletedEventQueue)
                        .to(eventExchange)
                        .with(MANAGER_DELETED_EVENT_ROUTING_KEY),

                BindingBuilder.bind(managerDeletionFailedEventQueue)
                        .to(eventExchange)
                        .with(MANAGER_DELETION_FAILED_EVENT_ROUTING_KEY)
        );
    }
}