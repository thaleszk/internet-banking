package com.internet.banking.customer.microservice.producer;
import com.internet.banking.customer.microservice.producer.*;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.internet.banking.customer.microservice.dto.CustomerCreatedEvent;

@Component
@RequiredArgsConstructor
public class CustomerProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendCustomerCreated(
            CustomerCreatedEvent event
    ) {
        rabbitTemplate.convertAndSend(
                "customer.created.queue",
                event
        );
    }


}