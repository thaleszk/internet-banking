package com.internet.banking.microservice.account.consumer;

import com.internet.banking.microservice.account.command.TransferAccountsCommand;
import com.internet.banking.microservice.account.config.DeleteManagerRabbitConstants;
import com.internet.banking.microservice.account.event.AccountsTransferFailedEvent;
import com.internet.banking.microservice.account.event.AccountsTransferredEvent;
import com.internet.banking.microservice.account.producer.DeleteManagerProducer;
import com.internet.banking.microservice.account.service.AccountService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransferAccountsConsumer {

    private final AccountService accountService;
    private final DeleteManagerProducer producer;

    public TransferAccountsConsumer(
            final AccountService accountService,
            final DeleteManagerProducer producer
    ) {
        this.accountService = accountService;
        this.producer = producer;
    }

    @RabbitListener(
            queues = DeleteManagerRabbitConstants.TRANSFER_ACCOUNTS_COMMAND_QUEUE
    )
    public void consume(
            final TransferAccountsCommand command
    ) {

        try {

            Integer transferred =
                    accountService.transferAccounts(
                            command.currentManagerCpf(),
                            command.replacementManagerCpf()
                    );

            producer.publishAccountsTransferred(
                    new AccountsTransferredEvent(
                            command.sagaId(),
                            transferred
                    )
            );

        } catch (Exception ex) {

            producer.publishAccountsTransferFailed(
                    new AccountsTransferFailedEvent(
                            command.sagaId(),
                            ex.getMessage()
                    )
            );

        }

    }

}