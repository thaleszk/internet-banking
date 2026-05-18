package com.internet.banking.microservice.account.service;

import com.internet.banking.microservice.account.data.TransactionHistoryData;
import com.internet.banking.microservice.account.model.AccountModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AccountService {

    AccountModel create(AccountModel accountModel);

    AccountModel findByNumber(String accountNumber);

    AccountModel deposit(String accountNumber, BigDecimal amount);

    AccountModel withdraw(String accountNumber, BigDecimal amount);

    // R7: Transferência
    AccountModel transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount);

    // R8: Extrato por período
    List<TransactionHistoryData> getStatement(String accountNumber, LocalDate startDate, LocalDate endDate);

    void delete(String accountNumber);
}