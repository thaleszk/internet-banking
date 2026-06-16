package com.internet.banking.microservice.account.dao;

import com.internet.banking.microservice.account.model.AccountReadModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountReadRepository extends JpaRepository<AccountReadModel, Long> {

    Optional<AccountReadModel> findByNumber(String number);

    List<AccountReadModel> findByCpfManager(String cpfManager);

    void deleteByNumber(String number);
}
