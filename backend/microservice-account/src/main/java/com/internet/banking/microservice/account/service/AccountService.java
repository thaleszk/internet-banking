package com.internet.banking.microservice.account.service;

import com.internet.banking.microservice.account.model.AccountModel;

public interface AccountService {

    AccountModel create(AccountModel accountModel);

    void delete(String accountNumber);
}