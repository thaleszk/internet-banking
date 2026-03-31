package com.internet.banking.microservice.account.dao;

import com.internet.banking.microservice.account.model.AccountModel;

public interface AccountDao {

    AccountModel save(AccountModel accountModel);

    void deleteByNumber(String accountNumber);

    AccountModel findByNumber(String accountNumber);
}
