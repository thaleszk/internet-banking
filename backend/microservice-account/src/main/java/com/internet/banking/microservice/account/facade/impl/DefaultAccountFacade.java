package com.internet.banking.microservice.account.facade.impl;

import com.internet.banking.microservice.account.data.AccountData;
import com.internet.banking.microservice.account.facade.AccountFacade;
import com.internet.banking.microservice.account.mapper.AccountMapper;
import com.internet.banking.microservice.account.model.AccountModel;
import com.internet.banking.microservice.account.service.AccountService;
import org.springframework.stereotype.Component;

@Component
public class DefaultAccountFacade implements AccountFacade {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public DefaultAccountFacade(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }

    @Override
    public AccountData create(AccountData accountData) {
        AccountModel accountModel = accountMapper.toModel(accountData);
        AccountModel savedAccount = accountService.create(accountModel);
        return accountMapper.toData(savedAccount);
    }

    @Override
    public void delete(String accountNumber) {
        accountService.delete(accountNumber);
    }
}