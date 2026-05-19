package com.internet.banking.microservice.account.dao;

import com.internet.banking.microservice.account.model.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository
        extends JpaRepository<AccountModel, Long> {

    List<AccountModel> findByCpfManager(
            String cpfManager
    );

    long countByCpfManager(
            String cpfManager
    );
}