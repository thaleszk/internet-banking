package com.internet.banking.microservice.account.service;

import com.internet.banking.microservice.account.model.AccountModel;
import java.math.BigDecimal;

public interface AccountService {

    AccountModel create(AccountModel accountModel);

    AccountModel findByNumber(String accountNumber);

    AccountModel deposit(String accountNumber, BigDecimal amount);

    AccountModel withdraw(String accountNumber, BigDecimal amount);

    void delete(String accountNumber);
}
