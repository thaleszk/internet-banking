package com.internet.banking.microservice.account.service.impl;

import com.internet.banking.microservice.account.dao.AccountDao;
import com.internet.banking.microservice.account.model.AccountModel;
import com.internet.banking.microservice.account.service.AccountService;
import java.math.BigDecimal;
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
    public AccountModel findByNumber(String accountNumber) {
        return getExistingAccount(accountNumber);
    }

    @Override
    public AccountModel deposit(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        AccountModel existingAccount = getExistingAccount(accountNumber);
        BigDecimal currentBalance = defaultIfNull(existingAccount.getBalance());
        existingAccount.setBalance(currentBalance.add(amount));

        return accountDAO.save(existingAccount);
    }

    @Override
    public AccountModel withdraw(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        AccountModel existingAccount = getExistingAccount(accountNumber);
        BigDecimal currentBalance = defaultIfNull(existingAccount.getBalance());
        BigDecimal currentLimit = defaultIfNull(existingAccount.getLimit());

        if (amount.compareTo(currentBalance.add(currentLimit)) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar o saque.");
        }

        existingAccount.setBalance(currentBalance.subtract(amount));

        return accountDAO.save(existingAccount);
    }

    @Override
    public void delete(String accountNumber) {
        getExistingAccount(accountNumber);
        accountDAO.deleteByNumber(accountNumber);
    }

    private AccountModel getExistingAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("O número da conta é obrigatório.");
        }

        AccountModel existingAccount = accountDAO.findByNumber(accountNumber);
        if (existingAccount == null) {
            throw new IllegalArgumentException("Conta não encontrada.");
        }

        return existingAccount;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da operação deve ser maior que zero.");
        }
    }

    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
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
