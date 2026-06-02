package com.internet.banking.customer.microservice.consumer;

import com.internet.banking.customer.microservice.dto.SagaErrorEvent;
import com.internet.banking.customer.microservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Component
@RequiredArgsConstructor
public class CustomerRollbackConsumer {

    private final CustomerService customerService;

    @RabbitListener(
            queues = "customer.rollback.queue"
    )
    public void rollback(
            SagaErrorEvent event
    ) {
        customerService.deleteCustomer(
                event.getCpf()
        );
    }
}