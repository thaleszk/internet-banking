package com.internet.banking.microservice_manager.producer;

import com.internet.banking.microservice_manager.config.DeleteManagerRabbitConstants;
import com.internet.banking.microservice_manager.event.ManagerDeletedEvent;
import com.internet.banking.microservice_manager.event.ManagerDeletionFailedEvent;
import com.internet.banking.microservice_manager.event.ReplacementManagerFoundEvent;
import com.internet.banking.microservice_manager.event.ReplacementManagerNotFoundEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerProducer {

    private final RabbitTemplate rabbitTemplate;

    public DeleteManagerProducer(
            RabbitTemplate rabbitTemplate
    ) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishReplacementManagerFound(
            ReplacementManagerFoundEvent event
    ) {

        rabbitTemplate.convertAndSend(
                DeleteManagerRabbitConstants.SAGA_EVENT_EXCHANGE,
                DeleteManagerRabbitConstants.REPLACEMENT_MANAGER_FOUND_EVENT_ROUTING_KEY,
                event
        );
    }

    public void publishReplacementManagerNotFound(
            ReplacementManagerNotFoundEvent event
    ) {

        rabbitTemplate.convertAndSend(
                DeleteManagerRabbitConstants.SAGA_EVENT_EXCHANGE,
                DeleteManagerRabbitConstants.REPLACEMENT_MANAGER_NOT_FOUND_EVENT_ROUTING_KEY,
                event
        );
    }

    public void publishManagerDeleted(
            ManagerDeletedEvent event
    ) {

        rabbitTemplate.convertAndSend(
                DeleteManagerRabbitConstants.SAGA_EVENT_EXCHANGE,
                DeleteManagerRabbitConstants.MANAGER_DELETED_EVENT_ROUTING_KEY,
                event
        );

    }

    public void publishManagerDeletionFailed(
            ManagerDeletionFailedEvent event
    ) {

        rabbitTemplate.convertAndSend(
                DeleteManagerRabbitConstants.SAGA_EVENT_EXCHANGE,
                DeleteManagerRabbitConstants.MANAGER_DELETION_FAILED_EVENT_ROUTING_KEY,
                event
        );

    }

}