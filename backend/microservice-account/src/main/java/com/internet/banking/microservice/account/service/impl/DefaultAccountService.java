package com.internet.banking.microservice.account.service.impl;

import com.internet.banking.microservice.account.dao.AccountRepository;
import com.internet.banking.microservice.account.data.TransactionHistoryData;
import com.internet.banking.microservice.account.enums.TransactionType;
import com.internet.banking.microservice.account.model.AccountModel;
import com.internet.banking.microservice.account.model.TransactionHistoryModel;
import com.internet.banking.microservice.account.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultAccountService implements AccountService {

    private final AccountRepository accountRepository;

    public DefaultAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public AccountModel create(AccountModel accountModel) {
        fillAccountNumber(accountModel);
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
    public List<AccountModel> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public List<AccountModel> findByManager(String cpfManager) {
        if (cpfManager == null || cpfManager.isBlank()) {
            throw new IllegalArgumentException("CPF do gerente e obrigatorio.");
        }
        return accountRepository.findByCpfManager(cpfManager);
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
    public AccountModel changeManager(String accountNumber, String cpfManager) {
        if (cpfManager == null || cpfManager.isBlank()) {
            throw new IllegalArgumentException("CPF do gerente e obrigatorio.");
        }
        AccountModel account = getExistingAccount(accountNumber);
        account.setCpfManager(cpfManager);
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

    // ── R7: Transferência ─────────────────────────────────────────────────────
    @Override
    @Transactional
    public AccountModel transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        validateAmount(amount);

        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new IllegalArgumentException("Conta origem e destino não podem ser iguais.");
        }

        AccountModel source = getExistingAccount(sourceAccountNumber);
        AccountModel destination = getExistingAccount(destinationAccountNumber);

        BigDecimal saldo = defaultIfNull(source.getBalance());
        BigDecimal limite = defaultIfNull(source.getLimit());

        if (amount.compareTo(saldo.add(limite)) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transferência.");
        }

        source.setBalance(saldo.subtract(amount));
        destination.setBalance(defaultIfNull(destination.getBalance()).add(amount));

        // Registra nas duas contas
        registrarTransacao(source, TransactionType.TRANSFERENCIA, source.getCpfCustomer(), destination.getCpfCustomer(), amount);
        registrarTransacao(destination, TransactionType.TRANSFERENCIA, source.getCpfCustomer(), destination.getCpfCustomer(), amount);

        accountRepository.save(destination);
        return accountRepository.save(source);
    }

    // ── R8: Extrato por período ───────────────────────────────────────────────
    @Override
    public List<TransactionHistoryData> getStatement(String accountNumber, LocalDate startDate, LocalDate endDate) {
        AccountModel account = getExistingAccount(accountNumber);

        LocalDateTime inicio = startDate.atStartOfDay();
        LocalDateTime fim = endDate.plusDays(1).atStartOfDay();

        return account.getTransactions().stream()
                .filter(tx -> tx.getDateTime().isAfter(inicio) && tx.getDateTime().isBefore(fim))
                .sorted((a, b) -> a.getDateTime().compareTo(b.getDateTime()))
                .map(this::toHistoryData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(String accountNumber) {
        getExistingAccount(accountNumber);
        accountRepository.deleteByNumber(accountNumber);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void registrarTransacao(AccountModel account, TransactionType type,
                                    String cpfOrigin, String cpfDest, BigDecimal amount) {
        TransactionHistoryModel tx = new TransactionHistoryModel();
        tx.setAccount(account);
        tx.setDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        tx.setType(type);
        tx.setCpfOrigin(cpfOrigin);
        tx.setCpfDest(cpfDest);
        tx.setAmount(amount);
        account.getTransactions().add(tx);
    }

    private TransactionHistoryData toHistoryData(TransactionHistoryModel tx) {
        TransactionHistoryData data = new TransactionHistoryData();
        data.setId(tx.getId());
        data.setDateTime(tx.getDateTime());
        data.setType(tx.getType().name());
        data.setCpfOrigin(tx.getCpfOrigin());
        data.setCpfDest(tx.getCpfDest());
        data.setAmount(tx.getAmount());
        return data;
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

    private void fillAccountNumber(AccountModel accountModel) {
        if (accountModel == null) {
            return;
        }
        if (accountModel.getNumber() != null && !accountModel.getNumber().isBlank()) {
            return;
        }

        String cpf = accountModel.getCpfCustomer() == null ? "" : accountModel.getCpfCustomer().replaceAll("\\D", "");
        if (cpf.length() >= 4) {
            accountModel.setNumber(cpf.substring(0, 4));
        }
    }

    private void validateAccount(AccountModel accountModel) {
        if (accountModel == null) throw new IllegalArgumentException("A conta não pode ser nula.");
        if (accountModel.getNumber() == null || accountModel.getNumber().isBlank())
            throw new IllegalArgumentException("O número da conta é obrigatório.");
        if (accountModel.getCpfCustomer() == null || accountModel.getCpfCustomer().isBlank())
            throw new IllegalArgumentException("O CPF do cliente é obrigatório.");
    }

    @Override
    @Transactional
    public Integer transferAccounts(
            final String currentManagerCpf,
            final String replacementManagerCpf
    ) {

        List<AccountModel> accounts =
                accountRepository.findByCpfManager(
                        currentManagerCpf
                );

        for (AccountModel account : accounts) {
            account.setCpfManager(replacementManagerCpf);
        }

        accountRepository.saveAll(accounts);

        return accounts.size();
    }
}
