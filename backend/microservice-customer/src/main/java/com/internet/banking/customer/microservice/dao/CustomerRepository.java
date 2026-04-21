package com.internet.banking.customer.microservice.dao;

import com.internet.banking.customer.microservice.model.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {

    Optional<CustomerModel> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    void deleteByCpf(String cpf);
}
