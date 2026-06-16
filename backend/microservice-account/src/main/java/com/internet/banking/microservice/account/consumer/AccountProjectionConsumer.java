package com.internet.banking.microservice.account.consumer;

import com.internet.banking.microservice.account.config.AccountCqrsRabbitConstants;
import com.internet.banking.microservice.account.dao.AccountReadRepository;
import com.internet.banking.microservice.account.dao.TransactionReadRepository;
import com.internet.banking.microservice.account.event.AccountProjectionEvent;
import com.internet.banking.microservice.account.event.TransactionProjectionEvent;
import com.internet.banking.microservice.account.model.AccountReadModel;
import com.internet.banking.microservice.account.model.TransactionReadModel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccountProjectionConsumer {

    private final AccountReadRepository accountReadRepository;
    private final TransactionReadRepository transactionReadRepository;

    public AccountProjectionConsumer(
            AccountReadRepository accountReadRepository,
            TransactionReadRepository transactionReadRepository
    ) {
        this.accountReadRepository = accountReadRepository;
        this.transactionReadRepository = transactionReadRepository;
    }

    @RabbitListener(queues = AccountCqrsRabbitConstants.ACCOUNT_PROJECTION_QUEUE)
    @Transactional
    public void consumeAccountProjection(AccountProjectionEvent event) {
        if (event.deleted()) {
            transactionReadRepository.deleteByAccountNumber(event.number());
            accountReadRepository.deleteByNumber(event.number());
            return;
        }

        AccountReadModel projection = accountReadRepository
                .findByNumber(event.number())
                .orElseGet(AccountReadModel::new);

        projection.setCpfCustomer(event.cpfCustomer());
        projection.setNumber(event.number());
        projection.setCreationDate(event.creationDate());
        projection.setBalance(event.balance());
        projection.setLimit(event.limit());
        projection.setCpfManager(event.cpfManager());
        projection.setUpdatedAt(event.occurredAt());

        accountReadRepository.save(projection);
    }

    @RabbitListener(queues = AccountCqrsRabbitConstants.TRANSACTION_PROJECTION_QUEUE)
    @Transactional
    public void consumeTransactionProjection(TransactionProjectionEvent event) {
        if (event.sourceTransactionId() == null) {
            return;
        }

        if (transactionReadRepository.findBySourceTransactionId(event.sourceTransactionId()).isPresent()) {
            return;
        }

        TransactionReadModel projection = new TransactionReadModel();
        projection.setSourceTransactionId(event.sourceTransactionId());
        projection.setAccountNumber(event.accountNumber());
        projection.setDateTime(event.dateTime());
        projection.setType(event.type());
        projection.setCpfOrigin(event.cpfOrigin());
        projection.setCpfDest(event.cpfDest());
        projection.setAmount(event.amount());

        transactionReadRepository.save(projection);
    }
}
