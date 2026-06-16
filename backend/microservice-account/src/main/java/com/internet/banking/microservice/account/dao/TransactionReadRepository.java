package com.internet.banking.microservice.account.dao;

import com.internet.banking.microservice.account.model.TransactionReadModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionReadRepository extends JpaRepository<TransactionReadModel, Long> {

    Optional<TransactionReadModel> findBySourceTransactionId(Long sourceTransactionId);

    List<TransactionReadModel> findByAccountNumberAndDateTimeAfterAndDateTimeBeforeOrderByDateTime(
            String accountNumber,
            LocalDateTime start,
            LocalDateTime end
    );

    void deleteByAccountNumber(String accountNumber);
}
