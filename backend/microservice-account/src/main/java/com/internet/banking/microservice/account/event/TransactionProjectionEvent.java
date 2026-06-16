package com.internet.banking.microservice.account.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionProjectionEvent(
        Long sourceTransactionId,
        String accountNumber,
        LocalDateTime dateTime,
        String type,
        String cpfOrigin,
        String cpfDest,
        BigDecimal amount
) {
}
