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


@Component
@RequiredArgsConstructor
public class CustomerConsumer {

    private final CustomerService customerService;
    private final CustomerProducer customerProducer;

    @RabbitListener(
            queues = "customer.create.queue"
    )
    public void createCustomer(
            CustomerRequest dto
    ) {

        CustomerData customer =
                customerService.createCustomer(CustomerDtoMapper.toData(dto));

        CustomerCreatedEvent event =
                new CustomerCreatedEvent(
                        customer.getId(),
                        customer.getCpf(),
                        customer.getEmail()
                );

        customerProducer.sendCustomerCreated(event);
    }
}