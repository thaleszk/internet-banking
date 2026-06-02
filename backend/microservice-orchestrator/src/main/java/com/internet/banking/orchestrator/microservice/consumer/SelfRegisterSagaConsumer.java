package com.internet.banking.orchestrator.microservice.consumer;

import com.internet.banking.orchestrator.microservice.dto.CustomerCreatedEvent;
import com.internet.banking.orchestrator.microservice.dto.SagaErrorEvent;
import com.internet.banking.orchestrator.microservice.producer.SagaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class SelfRegisterSagaConsumer {

    private final SagaEventProducer producer;
    private final ObjectMapper objectMapper;

    @RabbitListener(
            queues = "customer.created.queue"
    )
    public void onCustomerCreated(
                    String payload
    ) throws Exception {

        CustomerCreatedEvent request =
                objectMapper.readValue(
                        payload,
                        CustomerCreatedEvent.class
                );
        producer.send(
                "auth.create.queue",
                payload
        );
    }

    @RabbitListener(
            queues = "auth.failed.queue"
    )
    public void onAuthFailed(
            SagaErrorEvent event
    ) {

        producer.send(
                "customer.rollback.queue",
                event
        );
    }
}