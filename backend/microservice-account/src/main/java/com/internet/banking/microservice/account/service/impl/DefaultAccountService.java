package com.internet.banking.microservice.account.service.impl;

import com.internet.banking.microservice.account.dao.AccountDao;
import com.internet.banking.microservice.account.model.AccountModel;
import com.internet.banking.microservice.account.service.AccountService;
import org.springframework.stereotype.Service;

@Service
public class DefaultAccountService implements AccountService {

    private final AccountDao accountDAO;

    public DefaultAccountService(AccountDao accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public AccountModel create(AccountModel accountModel) {
        validateAccount(accountModel);

        AccountModel existingAccount = accountDAO.findByNumber(accountModel.getNumber());
        if (existingAccount != null) {
            throw new IllegalArgumentException("Já existe uma conta com esse número.");
        }

        return accountDAO.save(accountModel);
    }

    @Override
    public void delete(String accountNumber) {
        AccountModel existingAccount = accountDAO.findByNumber(accountNumber);
        if (existingAccount == null) {
            throw new IllegalArgumentException("Conta não encontrada para remoção.");
        }

        accountDAO.deleteByNumber(accountNumber);
    }

    private void validateAccount(AccountModel accountModel) {
        if (accountModel == null) {
            throw new IllegalArgumentException("A conta não pode ser nula.");
        }

        if (accountModel.getNumber() == null || accountModel.getNumber().isBlank()) {
            throw new IllegalArgumentException("O número da conta é obrigatório.");
        }

        if (accountModel.getCpfCustomer() == null || accountModel.getCpfCustomer().isBlank()) {
            throw new IllegalArgumentException("O CPF do cliente é obrigatório.");
        }
    }
}