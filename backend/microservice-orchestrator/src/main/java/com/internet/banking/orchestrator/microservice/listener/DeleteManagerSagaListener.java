package com.internet.banking.orchestrator.microservice.listener;

import com.internet.banking.orchestrator.microservice.config.DeleteManagerRabbitConstants;
import com.internet.banking.orchestrator.microservice.event.deleteManager.ReplacementManagerFoundEvent;
import com.internet.banking.orchestrator.microservice.event.deleteManager.ReplacementManagerNotFoundEvent;
import com.internet.banking.orchestrator.microservice.handler.DeleteManagerSagaHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerSagaListener {

    private final DeleteManagerSagaHandler sagaHandler;

    public DeleteManagerSagaListener(DeleteManagerSagaHandler sagaHandler) {
        this.sagaHandler = sagaHandler;
    }

    @RabbitListener(
            queues =
                    DeleteManagerRabbitConstants
                            .REPLACEMENT_MANAGER_FOUND_EVENT_QUEUE
    )
    public void consume(
            ReplacementManagerFoundEvent event
    ) {

        sagaHandler.handleReplacementManagerFound(
                event
        );
    }

    @RabbitListener(
            queues =
                    DeleteManagerRabbitConstants
                            .REPLACEMENT_MANAGER_NOT_FOUND_EVENT_QUEUE
    )
    public void consume(
            ReplacementManagerNotFoundEvent event
    ) {

        sagaHandler.handleReplacementManagerNotFound(
                event
        );
    }
}