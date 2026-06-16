package com.internet.banking.microservice.account.service.impl;

import com.internet.banking.microservice.account.dao.AccountReadRepository;
import com.internet.banking.microservice.account.dao.TransactionReadRepository;
import com.internet.banking.microservice.account.data.AccountData;
import com.internet.banking.microservice.account.data.TransactionHistoryData;
import com.internet.banking.microservice.account.model.AccountReadModel;
import com.internet.banking.microservice.account.model.TransactionReadModel;
import com.internet.banking.microservice.account.service.AccountQueryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DefaultAccountQueryService implements AccountQueryService {

    private final AccountReadRepository accountReadRepository;
    private final TransactionReadRepository transactionReadRepository;

    public DefaultAccountQueryService(
            AccountReadRepository accountReadRepository,
            TransactionReadRepository transactionReadRepository
    ) {
        this.accountReadRepository = accountReadRepository;
        this.transactionReadRepository = transactionReadRepository;
    }

    @Override
    public AccountData findByNumber(String accountNumber) {
        return accountReadRepository
                .findByNumber(accountNumber)
                .map(this::toAccountData)
                .orElseThrow(() -> new IllegalArgumentException("Conta nao encontrada no modelo de leitura: " + accountNumber));
    }

    @Override
    public List<AccountData> findAll() {
        return accountReadRepository.findAll()
                .stream()
                .map(this::toAccountData)
                .toList();
    }

    @Override
    public List<AccountData> findByManager(String cpfManager) {
        return accountReadRepository.findByCpfManager(cpfManager)
                .stream()
                .map(this::toAccountData)
                .toList();
    }

    @Override
    public List<TransactionHistoryData> getStatement(
            String accountNumber,
            LocalDate startDate,
            LocalDate endDate
    ) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        return transactionReadRepository
                .findByAccountNumberAndDateTimeAfterAndDateTimeBeforeOrderByDateTime(
                        accountNumber,
                        start,
                        end
                )
                .stream()
                .map(this::toTransactionData)
                .toList();
    }

    private AccountData toAccountData(AccountReadModel model) {
        AccountData data = new AccountData();
        data.setCpfCustomer(model.getCpfCustomer());
        data.setNumber(model.getNumber());
        data.setCreationDate(model.getCreationDate());
        data.setBalance(model.getBalance());
        data.setLimit(model.getLimit());
        data.setCpfManager(model.getCpfManager());
        return data;
    }

    private TransactionHistoryData toTransactionData(TransactionReadModel model) {
        TransactionHistoryData data = new TransactionHistoryData();
        data.setId(model.getSourceTransactionId());
        data.setDateTime(model.getDateTime());
        data.setType(model.getType());
        data.setCpfOrigin(model.getCpfOrigin());
        data.setCpfDest(model.getCpfDest());
        data.setAmount(model.getAmount());
        return data;
    }
}
