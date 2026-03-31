package com.internet.banking.microservice.account.dao.impl;

import com.internet.banking.microservice.account.dao.AccountDao;
import com.internet.banking.microservice.account.model.AccountModel;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryAccountDAO implements AccountDao {

    private final Map<String, AccountModel> accountStorage = new HashMap<>();

    @Override
    public AccountModel save(AccountModel accountModel) {
        accountStorage.put(accountModel.getNumber(), accountModel);
        return accountModel;
    }

    @Override
    public void deleteByNumber(String accountNumber) {
        accountStorage.remove(accountNumber);
    }

    @Override
    public AccountModel findByNumber(String accountNumber) {
        return accountStorage.get(accountNumber);
    }
}