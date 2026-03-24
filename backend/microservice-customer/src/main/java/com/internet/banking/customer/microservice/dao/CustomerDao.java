package com.internet.banking.customer.microservice.dao;

import com.internet.banking.customer.microservice.data.CustomerData;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {

    CustomerData save(CustomerData customerData);

    Optional<CustomerData> findByCpf(String cpf);

    List<CustomerData> findAll();

    CustomerData update(String cpf, CustomerData customerData);

    void delete(String cpf);

    boolean existsByCpf(String cpf);
}
