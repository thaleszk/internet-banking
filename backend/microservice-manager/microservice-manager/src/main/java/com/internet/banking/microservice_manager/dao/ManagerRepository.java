package com.internet.banking.microservice_manager.dao;

import com.internet.banking.microservice_manager.model.ManagerModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<ManagerModel, Long> {

    Optional<ManagerModel> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    void deleteByCpf(String cpf);
}
