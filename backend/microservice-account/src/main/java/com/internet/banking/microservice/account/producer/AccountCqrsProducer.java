package com.internet.banking.microservice.account.producer;

import com.internet.banking.microservice.account.config.AccountCqrsRabbitConstants;
import com.internet.banking.microservice.account.event.AccountProjectionEvent;
import com.internet.banking.microservice.account.event.TransactionProjectionEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccountCqrsProducer {

    private final RabbitTemplate rabbitTemplate;

    public AccountCqrsProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAccountProjection(AccountProjectionEvent event) {
        rabbitTemplate.convertAndSend(
                AccountCqrsRabbitConstants.ACCOUNT_CQRS_EXCHANGE,
                AccountCqrsRabbitConstants.ACCOUNT_PROJECTION_ROUTING_KEY,
                event
        );
    }

    public void publishTransactionProjection(TransactionProjectionEvent event) {
        rabbitTemplate.convertAndSend(
                AccountCqrsRabbitConstants.ACCOUNT_CQRS_EXCHANGE,
                AccountCqrsRabbitConstants.TRANSACTION_PROJECTION_ROUTING_KEY,
                event
        );
    }
}
