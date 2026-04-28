package com.internet.banking.microservice.account.facade;

import com.internet.banking.microservice.account.data.AccountData;
import java.math.BigDecimal;

public interface AccountFacade {

    AccountData create(AccountData accountData);

    AccountData findByNumber(String accountNumber);

    AccountData deposit(String accountNumber, BigDecimal amount);

    AccountData withdraw(String accountNumber, BigDecimal amount);

    void delete(String accountNumber);
}
