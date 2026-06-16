package com.internet.banking.microservice.account.producer;

import com.internet.banking.microservice.account.event.AccountsTransferFailedEvent;
import com.internet.banking.microservice.account.event.AccountsTransferredEvent;
import com.internet.banking.microservice.account.config.DeleteManagerRabbitConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerProducer {

    private final RabbitTemplate rabbitTemplate;

    public DeleteManagerProducer(
            final RabbitTemplate rabbitTemplate
    ) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAccountsTransferred(
            final AccountsTransferredEvent event
    ) {

        rabbitTemplate.convertAndSend(
                DeleteManagerRabbitConstants.SAGA_EVENT_EXCHANGE,
                DeleteManagerRabbitConstants.ACCOUNTS_TRANSFERRED_EVENT_ROUTING_KEY,
                event
        );

    }

    public void publishAccountsTransferFailed(
            final AccountsTransferFailedEvent event
    ) {

        rabbitTemplate.convertAndSend(
                DeleteManagerRabbitConstants.SAGA_EVENT_EXCHANGE,
                DeleteManagerRabbitConstants.ACCOUNTS_TRANSFER_FAILED_EVENT_ROUTING_KEY,
                event
        );

    }

}