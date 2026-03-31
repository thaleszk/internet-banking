package com.internet.banking.microservice.account.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountData {

    private String cpfCustomer;
    private String number;
    private LocalDate creationDate;
    private BigDecimal balance;
    private BigDecimal limit;
    private String cpfManager;

    public AccountData() {
    }

    public String getCpfCustomer() {
        return cpfCustomer;
    }

    public void setCpfCustomer(String cpfCustomer) {
        this.cpfCustomer = cpfCustomer;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public String getCpfManager() {
        return cpfManager;
    }

    public void setCpfManager(String cpfManager) {
        this.cpfManager = cpfManager;
    }
}