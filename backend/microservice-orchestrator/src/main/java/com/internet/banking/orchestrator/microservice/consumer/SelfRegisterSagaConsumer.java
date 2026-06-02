package com.internet.banking.orchestrator.microservice.consumer;

import com.internet.banking.orchestrator.microservice.dto.CustomerCreatedEvent;
import com.internet.banking.orchestrator.microservice.dto.SagaErrorEvent;
import com.internet.banking.orchestrator.microservice.producer.SagaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SelfRegisterSagaConsumer {

    private final SagaEventProducer producer;

    @RabbitListener(
            queues = "customer.created.queue"
    )
    public void onCustomerCreated(
            CustomerCreatedEvent event
    ) {

        producer.send(
                "auth.create.queue",
                event
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