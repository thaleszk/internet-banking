package com.internet.banking.microservice_manager.consumer;

import com.internet.banking.microservice_manager.command.DeleteManagerCommand;
import com.internet.banking.microservice_manager.config.DeleteManagerRabbitConstants;
import com.internet.banking.microservice_manager.event.ManagerDeletedEvent;
import com.internet.banking.microservice_manager.event.ManagerDeletionFailedEvent;
import com.internet.banking.microservice_manager.producer.DeleteManagerProducer;
import com.internet.banking.microservice_manager.service.ManagerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerConsumer {

    private final ManagerService managerService;
    private final DeleteManagerProducer producer;

    public DeleteManagerConsumer(
            final ManagerService managerService,
            final DeleteManagerProducer producer
    ) {
        this.managerService = managerService;
        this.producer = producer;
    }

    @RabbitListener(
            queues = DeleteManagerRabbitConstants.DELETE_MANAGER_COMMAND_QUEUE
    )
    public void consume(
            final DeleteManagerCommand command
    ) {

        try {

            managerService.deleteManager(
                    command.correlationKey()
            );

            producer.publishManagerDeleted(
                    new ManagerDeletedEvent(
                            command.sagaId()
                    )
            );

        } catch (Exception ex) {

            producer.publishManagerDeletionFailed(
                    new ManagerDeletionFailedEvent(
                            command.sagaId(),
                            ex.getMessage()
                    )
            );

        }

    }

}