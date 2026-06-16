package com.internet.banking.microservice.account.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_read_model")
public class AccountReadModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf_customer", nullable = false, unique = true)
    private String cpfCustomer;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "account_limit", nullable = false)
    private BigDecimal limit = BigDecimal.ZERO;

    @Column(name = "cpf_manager", nullable = false)
    private String cpfManager;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AccountReadModel() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCpfCustomer() { return cpfCustomer; }
    public void setCpfCustomer(String cpfCustomer) { this.cpfCustomer = cpfCustomer; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getLimit() { return limit; }
    public void setLimit(BigDecimal limit) { this.limit = limit; }

    public String getCpfManager() { return cpfManager; }
    public void setCpfManager(String cpfManager) { this.cpfManager = cpfManager; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
