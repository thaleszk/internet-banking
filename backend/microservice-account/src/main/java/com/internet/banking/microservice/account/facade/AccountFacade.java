package com.internet.banking.microservice.account.facade;

import com.internet.banking.microservice.account.data.AccountData;

public interface AccountFacade {

    AccountData create(AccountData accountData);

    void delete(String accountNumber);
}