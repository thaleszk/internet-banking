package com.internet.banking.microservice.account.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_read_model")
public class TransactionReadModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_transaction_id", nullable = false, unique = true)
    private Long sourceTransactionId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "cpf_origin")
    private String cpfOrigin;

    @Column(name = "cpf_dest")
    private String cpfDest;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    public TransactionReadModel() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSourceTransactionId() { return sourceTransactionId; }
    public void setSourceTransactionId(Long sourceTransactionId) { this.sourceTransactionId = sourceTransactionId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCpfOrigin() { return cpfOrigin; }
    public void setCpfOrigin(String cpfOrigin) { this.cpfOrigin = cpfOrigin; }

    public String getCpfDest() { return cpfDest; }
    public void setCpfDest(String cpfDest) { this.cpfDest = cpfDest; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
