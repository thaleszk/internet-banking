package com.internet.banking.microservice.account.model;

import com.internet.banking.microservice.account.enums.TransactionType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
public class TransactionHistoryModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountModel account;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @Column(name = "cpf_origin")
    private String cpfOrigin;

    @Column(name = "cpf_dest")
    private String cpfDest;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    public TransactionHistoryModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AccountModel getAccount() { return account; }
    public void setAccount(AccountModel account) { this.account = account; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getCpfOrigin() { return cpfOrigin; }
    public void setCpfOrigin(String cpfOrigin) { this.cpfOrigin = cpfOrigin; }

    public String getCpfDest() { return cpfDest; }
    public void setCpfDest(String cpfDest) { this.cpfDest = cpfDest; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}