package com.internet.banking.orchestrator.microservice.consumer;

import com.internet.banking.orchestrator.microservice.config.DeleteManagerRabbitConstants;
import com.internet.banking.orchestrator.microservice.event.deleteManager.*;
import com.internet.banking.orchestrator.microservice.handler.DeleteManagerSagaHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerSagaConsumer {

    private final DeleteManagerSagaHandler sagaHandler;

    public DeleteManagerSagaConsumer(
            DeleteManagerSagaHandler sagaHandler
    ) {
        this.sagaHandler = sagaHandler;
    }

    @RabbitListener(
            queues = DeleteManagerRabbitConstants.REPLACEMENT_MANAGER_FOUND_EVENT_QUEUE
    )
    public void consume(
            ReplacementManagerFoundEvent event
    ) {
        sagaHandler.handleReplacementManagerFound(event);
    }

    @RabbitListener(
            queues = DeleteManagerRabbitConstants.REPLACEMENT_MANAGER_NOT_FOUND_EVENT_QUEUE
    )
    public void consume(
            ReplacementManagerNotFoundEvent event
    ) {
        sagaHandler.handleReplacementManagerNotFound(event);
    }

    @RabbitListener(
            queues = DeleteManagerRabbitConstants.ACCOUNTS_TRANSFERRED_EVENT_QUEUE
    )
    public void consume(
            AccountsTransferredEvent event
    ) {
        sagaHandler.handleAccountsTransferred(event);
    }

    @RabbitListener(
            queues = DeleteManagerRabbitConstants.ACCOUNTS_TRANSFER_FAILED_EVENT_QUEUE
    )
    public void consume(
            AccountsTransferFailedEvent event
    ) {
        sagaHandler.handleAccountsTransferFailed(event);
    }

    @RabbitListener(
            queues = DeleteManagerRabbitConstants.MANAGER_DELETED_EVENT_QUEUE
    )
    public void consume(
            ManagerDeletedEvent event
    ) {
        sagaHandler.handleManagerDeleted(event);
    }
}