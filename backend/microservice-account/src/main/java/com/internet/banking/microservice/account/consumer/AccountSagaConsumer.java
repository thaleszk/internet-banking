package com.internet.banking.microservice.account.consumer;

import com.internet.banking.microservice.account.data.DeleteManagerEventData;
import com.internet.banking.microservice.account.service.AccountSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.internet.banking.microservice.account.config.RabbitConfig.VALIDATED_QUEUE;

@Component
@RequiredArgsConstructor
public class AccountSagaConsumer {

    private final AccountSagaService service;

    @RabbitListener(queues = VALIDATED_QUEUE)
    public void receiveValidatedDeletion(
            DeleteManagerEventData event
    ) {

        service.transferAccounts(event);
    }
}