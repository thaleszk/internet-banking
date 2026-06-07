package com.internet.banking.microservice.account.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionHistoryData {

    private Long id;
    private LocalDateTime dateTime;
    private String type;
    private String cpfOrigin;
    private String cpfDest;
    private BigDecimal amount;

    public TransactionHistoryData() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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