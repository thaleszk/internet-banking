package com.internet.banking.manager.microservice.dao;

import com.internet.banking.manager.microservice.model.ManagerModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<ManagerModel, Long> {

    Optional<ManagerModel> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    void deleteByCpf(String cpf);
}
