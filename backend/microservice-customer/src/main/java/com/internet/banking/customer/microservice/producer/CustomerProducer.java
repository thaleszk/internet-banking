package com.internet.banking.customer.microservice.producer;
import com.internet.banking.customer.microservice.dto.CustomerCreatedEvent;
import com.internet.banking.customer.microservice.producer.*;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
public class CustomerProducer {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendCustomerCreated(
            CustomerCreatedEvent event
    ) {
        send(
                "customer.created.queue",
                event
        );
    }

    public void send(
            String queue,
            Object payload
    ) {

        String json =
                objectMapper.writeValueAsString(payload);

        rabbitTemplate.convertAndSend(
                queue,
                json
        );

    }
}