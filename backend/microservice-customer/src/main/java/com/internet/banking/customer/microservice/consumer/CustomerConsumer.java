package com.internet.banking.customer.microservice.consumer;

import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.dto.CustomerCreatedEvent;
import com.internet.banking.customer.microservice.dto.request.CustomerRequest;
import com.internet.banking.customer.microservice.mapper.CustomerDtoMapper;
import com.internet.banking.customer.microservice.model.CustomerModel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.internet.banking.customer.microservice.service.CustomerService;
import com.internet.banking.customer.microservice.producer.CustomerProducer;
import com.internet.banking.customer.microservice.service.CustomerService;
import tools.jackson.databind.ObjectMapper;


@Component
@RequiredArgsConstructor
public class CustomerConsumer {

    private final CustomerService customerService;
    private final CustomerProducer customerProducer;
    private final ObjectMapper objectMapper;

    @RabbitListener(
            queues = "customer.create.queue"
    )
    public void createCustomer(
            String payload
    ) throws Exception {

        CustomerRequest request =
                objectMapper.readValue(
                        payload,
                        CustomerRequest.class
                );

        CustomerData customer = customerService.createCustomer(CustomerDtoMapper.toData(request));

        CustomerCreatedEvent event =
                new CustomerCreatedEvent(
                        customer.getId(),
                        customer.getCpf(),
                        customer.getEmail()
                );

        customerProducer.sendCustomerCreated(event);
    }
}