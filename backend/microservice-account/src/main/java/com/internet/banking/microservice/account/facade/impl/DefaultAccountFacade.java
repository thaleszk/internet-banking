package com.internet.banking.microservice.account.facade.impl;

import com.internet.banking.microservice.account.data.AccountData;
import com.internet.banking.microservice.account.data.TransactionHistoryData;
import com.internet.banking.microservice.account.facade.AccountFacade;
import com.internet.banking.microservice.account.mapper.AccountMapper;
import com.internet.banking.microservice.account.model.AccountModel;
import com.internet.banking.microservice.account.service.AccountService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    public AccountData findByNumber(String accountNumber) {
        AccountModel accountModel = accountService.findByNumber(accountNumber);
        return accountMapper.toData(accountModel);
    }

    @Override
    public List<AccountData> findAll() {
        return accountService.findAll()
                .stream()
                .map(accountMapper::toData)
                .toList();
    }

    @Override
    public List<AccountData> findByManager(String cpfManager) {
        return accountService.findByManager(cpfManager)
                .stream()
                .map(accountMapper::toData)
                .toList();
    }

    @Override
    public AccountData deposit(String accountNumber, BigDecimal amount) {
        AccountModel updatedAccount = accountService.deposit(accountNumber, amount);
        return accountMapper.toData(updatedAccount);
    }

    @Override
    public AccountData withdraw(String accountNumber, BigDecimal amount) {
        AccountModel updatedAccount = accountService.withdraw(accountNumber, amount);
        return accountMapper.toData(updatedAccount);
    }

    @Override
    public AccountData changeManager(String accountNumber, String cpfManager) {
        AccountModel updatedAccount = accountService.changeManager(accountNumber, cpfManager);
        return accountMapper.toData(updatedAccount);
    }

    @Override
    public AccountData transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        AccountModel updatedSource = accountService.transfer(sourceAccountNumber, destinationAccountNumber, amount);
        return accountMapper.toData(updatedSource);
    }

    @Override
    public List<TransactionHistoryData> getStatement(String accountNumber, LocalDate startDate, LocalDate endDate) {
        return accountService.getStatement(accountNumber, startDate, endDate);
    }

    @Override
    public void delete(String accountNumber) {
        accountService.delete(accountNumber);
    }
}
