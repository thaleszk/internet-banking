package com.internet.banking.microservice.account.controller;

import com.internet.banking.microservice.account.data.AccountData;
import com.internet.banking.microservice.account.data.TransactionHistoryData;
import com.internet.banking.microservice.account.data.TransferData;
import com.internet.banking.microservice.account.facade.AccountFacade;
import java.math.BigDecimal;
import java.util.List;
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

    // ── Criar conta ───────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<AccountData> createAccount(@RequestBody AccountData accountData) {
        AccountData createdAccount = accountFacade.create(accountData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    // ── Buscar conta por número ───────────────────────────────────────────────
    @GetMapping("/{number}")
    public ResponseEntity<AccountData> findAccountByNumber(@PathVariable String number) {
        AccountData account = accountFacade.findByNumber(number);
        return ResponseEntity.ok(account);
    }

    // ── R5: Depósito ──────────────────────────────────────────────────────────
    @PutMapping("/{number}/deposit")
    public ResponseEntity<AccountData> deposit(
            @PathVariable String number,
            @RequestParam BigDecimal amount
    ) {
        AccountData updatedAccount = accountFacade.deposit(number, amount);
        return ResponseEntity.ok(updatedAccount);
    }

    // ── R6: Saque ─────────────────────────────────────────────────────────────
    @PutMapping("/{number}/withdraw")
    public ResponseEntity<AccountData> withdraw(
            @PathVariable String number,
            @RequestParam BigDecimal amount
    ) {
        AccountData updatedAccount = accountFacade.withdraw(number, amount);
        return ResponseEntity.ok(updatedAccount);
    }

    // ── R7: Transferência ─────────────────────────────────────────────────────
    @PostMapping("/transfer")
    public ResponseEntity<AccountData> transfer(@RequestBody TransferData transferData) {
        AccountData updatedAccount = accountFacade.transfer(
                transferData.getSourceAccountNumber(),
                transferData.getDestinationAccountNumber(),
                transferData.getAmount()
        );
        return ResponseEntity.ok(updatedAccount);
    }

    // ── R8: Extrato por período ───────────────────────────────────────────────
    @GetMapping("/{number}/statement")
    public ResponseEntity<List<TransactionHistoryData>> getStatement(
            @PathVariable String number,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<TransactionHistoryData> statement = accountFacade.getStatement(number, startDate, endDate);
        return ResponseEntity.ok(statement);
    }

    // ── Deletar conta ─────────────────────────────────────────────────────────
    @DeleteMapping("/{number}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String number) {
        accountFacade.delete(number);
        return ResponseEntity.noContent().build();
    }
}