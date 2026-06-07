package com.internet.banking.microservice_manager.consumer;

import com.internet.banking.microservice_manager.dto.DeleteManagerEvent;
import com.internet.banking.microservice_manager.service.ManagerSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.internet.banking.microservice_manager.config.RabbitConfig.DELETE_REQUEST_QUEUE;

@Component
@RequiredArgsConstructor
public class ManagerSagaConsumer {

    private final ManagerSagaService service;

    @RabbitListener(queues = DELETE_REQUEST_QUEUE)
    public void receiveDeleteRequest(
            DeleteManagerEvent event
    ) {

        service.validateDeletion(event);
    }
}