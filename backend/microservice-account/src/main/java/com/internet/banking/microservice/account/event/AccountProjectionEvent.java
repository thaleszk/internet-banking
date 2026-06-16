package com.internet.banking.microservice.account.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AccountProjectionEvent(
        String cpfCustomer,
        String number,
        LocalDate creationDate,
        BigDecimal balance,
        BigDecimal limit,
        String cpfManager,
        LocalDateTime occurredAt,
        boolean deleted
) {
}
