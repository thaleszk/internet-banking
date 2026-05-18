package com.internet.banking.microservice.account.facade;

import com.internet.banking.microservice.account.data.AccountData;
import com.internet.banking.microservice.account.data.TransactionHistoryData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AccountFacade {

    AccountData create(AccountData accountData);

    AccountData findByNumber(String accountNumber);

    AccountData deposit(String accountNumber, BigDecimal amount);

    AccountData withdraw(String accountNumber, BigDecimal amount);

    // R7: Transferência
    AccountData transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount);

    // R8: Extrato
    List<TransactionHistoryData> getStatement(String accountNumber, LocalDate startDate, LocalDate endDate);

    void delete(String accountNumber);
}