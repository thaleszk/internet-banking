package com.internet.banking.microservice.account.service;

import com.internet.banking.microservice.account.data.AccountData;
import com.internet.banking.microservice.account.data.TransactionHistoryData;

import java.time.LocalDate;
import java.util.List;

public interface AccountQueryService {

    AccountData findByNumber(String accountNumber);

    List<AccountData> findAll();

    List<AccountData> findByManager(String cpfManager);

    List<TransactionHistoryData> getStatement(String accountNumber, LocalDate startDate, LocalDate endDate);
}
