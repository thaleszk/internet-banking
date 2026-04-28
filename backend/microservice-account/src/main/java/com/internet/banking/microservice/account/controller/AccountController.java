package com.internet.banking.microservice.account.controller;

import com.internet.banking.microservice.account.data.AccountData;
import com.internet.banking.microservice.account.facade.AccountFacade;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{number}")
    public ResponseEntity<AccountData> findAccountByNumber(@PathVariable String number) {
        AccountData account = accountFacade.findByNumber(number);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{number}/deposit")
    public ResponseEntity<AccountData> deposit(
            @PathVariable String number,
            @RequestParam BigDecimal amount
    ) {
        AccountData updatedAccount = accountFacade.deposit(number, amount);
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

    @DeleteMapping("/{number}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String number) {
        accountFacade.delete(number);
        return ResponseEntity.noContent().build();
    }
}
