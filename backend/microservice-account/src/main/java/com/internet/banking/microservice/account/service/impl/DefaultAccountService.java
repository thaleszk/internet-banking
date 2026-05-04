package com.internet.banking.microservice.account.service.impl;

import com.internet.banking.microservice.account.dao.AccountRepository;
import com.internet.banking.microservice.account.enums.TransactionType;
import com.internet.banking.microservice.account.model.AccountModel;
import com.internet.banking.microservice.account.model.TransactionHistoryModel;
import com.internet.banking.microservice.account.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class DefaultAccountService implements AccountService {

    private final AccountRepository accountRepository;

    public DefaultAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public AccountModel create(AccountModel accountModel) {
        validateAccount(accountModel);

        if (accountRepository.existsByNumber(accountModel.getNumber())) {
            throw new IllegalArgumentException("Já existe uma conta com esse número.");
        }

        return accountRepository.save(accountModel);
    }

    @Override
    public AccountModel findByNumber(String accountNumber) {
        return getExistingAccount(accountNumber);
    }

    @Override
    @Transactional
    public AccountModel deposit(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        AccountModel account = getExistingAccount(accountNumber);
        account.setBalance(defaultIfNull(account.getBalance()).add(amount));

        registrarTransacao(account, TransactionType.DEPOSITO, null, null, amount);

        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public AccountModel withdraw(String accountNumber, BigDecimal amount) {
        validateAmount(amount);

        AccountModel account = getExistingAccount(accountNumber);
        BigDecimal saldo = defaultIfNull(account.getBalance());
        BigDecimal limite = defaultIfNull(account.getLimit());

        if (amount.compareTo(saldo.add(limite)) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar o saque.");
        }

        account.setBalance(saldo.subtract(amount));

        registrarTransacao(account, TransactionType.SAQUE, null, null, amount);

        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public void delete(String accountNumber) {
        getExistingAccount(accountNumber);
        accountRepository.deleteByNumber(accountNumber);
    }

    // ── Helper: registra transação no histórico ───────────────────────────────
    private void registrarTransacao(AccountModel account, TransactionType type,
                                    String cpfOrigin, String cpfDest, BigDecimal amount) {
        TransactionHistoryModel tx = new TransactionHistoryModel();
        tx.setAccount(account);
        tx.setDateTime(LocalDateTime.now());
        tx.setType(type);
        tx.setCpfOrigin(cpfOrigin);
        tx.setCpfDest(cpfDest);
        tx.setAmount(amount);
        account.getTransactions().add(tx);
    }

    private AccountModel getExistingAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("O número da conta é obrigatório.");
        }
        return accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada: " + accountNumber));
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
        if (accountModel == null) throw new IllegalArgumentException("A conta não pode ser nula.");
        if (accountModel.getNumber() == null || accountModel.getNumber().isBlank())
            throw new IllegalArgumentException("O número da conta é obrigatório.");
        if (accountModel.getCpfCustomer() == null || accountModel.getCpfCustomer().isBlank())
            throw new IllegalArgumentException("O CPF do cliente é obrigatório.");
    }
}