package com.internet.banking.microservice.account.controller;

import com.internet.banking.microservice.account.data.AccountData;
import com.internet.banking.microservice.account.data.TransactionHistoryData;
import com.internet.banking.microservice.account.data.TransferData;
import com.internet.banking.microservice.account.facade.AccountFacade;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountFacade accountFacade;

    public AccountController(AccountFacade accountFacade) {
        this.accountFacade = accountFacade;
    }

    @PostMapping
    public ResponseEntity<AccountData> createAccount(@RequestBody AccountData accountData) {
        AccountData createdAccount = accountFacade.create(accountData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @GetMapping
    public ResponseEntity<List<AccountData>> findAllAccounts() {
        return ResponseEntity.ok(accountFacade.findAll());
    }

    @GetMapping("/manager/{cpfManager}")
    public ResponseEntity<List<AccountData>> findAccountsByManager(@PathVariable String cpfManager) {
        return ResponseEntity.ok(accountFacade.findByManager(cpfManager));
    }

    @GetMapping("/{number}")
    public ResponseEntity<AccountData> findAccountByNumber(@PathVariable String number) {
        AccountData account = accountFacade.findByNumber(number);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{number}/saldo")
    public ResponseEntity<Map<String, Object>> findBalanceByNumber(@PathVariable String number) {
        AccountData account = accountFacade.findByNumber(number);
        return ResponseEntity.ok(Map.of(
                "saldo", account.getBalance(),
                "balance", account.getBalance(),
                "cliente", account.getCpfCustomer(),
                "conta", account.getNumber()
        ));
    }

    @PutMapping("/{number}/deposit")
    public ResponseEntity<AccountData> deposit(
            @PathVariable String number,
            @RequestParam BigDecimal amount
    ) {
        AccountData updatedAccount = accountFacade.deposit(number, amount);
        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/{number}/depositar")
    public ResponseEntity<Map<String, Object>> depositByBody(
            @PathVariable String number,
            @RequestBody Map<String, Object> body
    ) {
        AccountData updatedAccount = accountFacade.deposit(number, amountFromBody(body));
        return ResponseEntity.ok(operationResponse(updatedAccount, "data"));
    }

    @PutMapping("/{number}/manager")
    public ResponseEntity<AccountData> changeManager(
            @PathVariable String number,
            @RequestBody Map<String, String> body
    ) {
        AccountData updatedAccount = accountFacade.changeManager(number, body.get("cpfManager"));
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/{number}/withdraw")
    public ResponseEntity<AccountData> withdraw(
            @PathVariable String number,
            @RequestParam BigDecimal amount
    ) {
        AccountData updatedAccount = accountFacade.withdraw(number, amount);
        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/{number}/sacar")
    public ResponseEntity<Map<String, Object>> withdrawByBody(
            @PathVariable String number,
            @RequestBody Map<String, Object> body
    ) {
        AccountData updatedAccount = accountFacade.withdraw(number, amountFromBody(body));
        return ResponseEntity.ok(operationResponse(updatedAccount, "data"));
    }

    @PostMapping("/transfer")
    public ResponseEntity<AccountData> transfer(@RequestBody TransferData transferData) {
        AccountData updatedAccount = accountFacade.transfer(
                transferData.getSourceAccountNumber(),
                transferData.getDestinationAccountNumber(),
                transferData.getAmount()
        );
        return ResponseEntity.ok(updatedAccount);
    }

    @PostMapping("/{number}/transferir")
    public ResponseEntity<Map<String, Object>> transferByBody(
            @PathVariable String number,
            @RequestBody Map<String, Object> body
    ) {
        String destination = valueAsString(body == null ? null : body.get("destino"));
        BigDecimal amount = amountFromBody(body);
        AccountData updatedAccount = accountFacade.transfer(number, destination, amount);

        Map<String, Object> response = operationResponse(updatedAccount, "data");
        response.put("destino", destination);
        response.put("valor", amount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{number}/statement")
    public ResponseEntity<List<TransactionHistoryData>> getStatement(
            @PathVariable String number,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<TransactionHistoryData> statement = accountFacade.getStatement(number, startDate, endDate);
        return ResponseEntity.ok(statement);
    }

    @GetMapping("/{number}/extrato")
    public ResponseEntity<Map<String, Object>> getFullStatement(@PathVariable String number) {
        AccountData account = accountFacade.findByNumber(number);
        List<Map<String, Object>> transactions = accountFacade
                .getStatement(number, LocalDate.of(1900, 1, 1), LocalDate.now().plusDays(1))
                .stream()
                .sorted(Comparator.comparing(TransactionHistoryData::getDateTime))
                .map(transaction -> statementItem(number, transaction))
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("conta", account.getNumber());
        response.put("cpf", account.getCpfCustomer());
        response.put("cliente", account.getCpfCustomer());
        response.put("saldo", account.getBalance());
        response.put("movimentacoes", transactions);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{number}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String number) {
        accountFacade.delete(number);
        return ResponseEntity.noContent().build();
    }

    private BigDecimal amountFromBody(Map<String, Object> body) {
        Object value = body == null ? null : body.get("valor");
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    private Map<String, Object> operationResponse(AccountData account, String dateField) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("conta", account.getNumber());
        response.put("cpf", account.getCpfCustomer());
        response.put("cliente", account.getCpfCustomer());
        response.put("saldo", account.getBalance());
        response.put(dateField, lastTransactionDate(account.getNumber()));
        return response;
    }

    private Map<String, Object> statementItem(String accountNumber, TransactionHistoryData transaction) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("tipo", transactionType(transaction.getType()));
        item.put("origem", accountNumber);
        if ("TRANSFERENCIA".equals(transaction.getType())) {
            item.put("destino", accountNumberByCpf(transaction.getCpfDest()));
        }
        item.put("data", formatDate(transaction.getDateTime()));
        item.put("valor", transaction.getAmount());
        return item;
    }

    private String lastTransactionDate(String number) {
        return accountFacade.getStatement(number, LocalDate.now(), LocalDate.now())
                .stream()
                .max(Comparator.comparing(TransactionHistoryData::getDateTime))
                .map(TransactionHistoryData::getDateTime)
                .map(this::formatDate)
                .orElse(formatDate(LocalDateTime.now()));
    }

    private String transactionType(String type) {
        return switch (type) {
            case "DEPOSITO" -> "depósito";
            case "SAQUE" -> "saque";
            case "TRANSFERENCIA" -> "transferência";
            default -> type;
        };
    }

    private String accountNumberByCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return "";
        }
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() >= 4) {
            return digits.substring(0, 4);
        }
        return digits;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.toString();
    }

    private String valueAsString(Object value) {
        return value == null ? "" : value.toString();
    }
}
