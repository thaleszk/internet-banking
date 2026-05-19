package com.internet.banking.microservice.account.producer;

import com.internet.banking.microservice.account.data.TransferCompletedEventData;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.internet.banking.microservice.account.config.RabbitConfig.EXCHANGE;

@Component
@RequiredArgsConstructor
public class AccountSagaProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTransferCompleted(
            TransferCompletedEventData event
    ) {

        rabbitTemplate.convertAndSend(
                EXCHANGE,
                "account.transfer.completed",
                event
        );
    }
}