package com.internet.banking.customer.microservice.dao.impl;

import com.internet.banking.customer.microservice.dao.CustomerDao;
import com.internet.banking.customer.microservice.data.CustomerData;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryCustomerDao implements CustomerDao {

    private final ConcurrentMap<String, CustomerData> database = new ConcurrentHashMap<>();

    @Override
    public CustomerData save(final CustomerData customerData) {
        database.put(customerData.getCpf(), customerData);
        return customerData;
    }

    @Override
    public Optional<CustomerData> findByCpf(final String cpf) {
        return Optional.ofNullable(database.get(cpf));
    }

    @Override
    public List<CustomerData> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public CustomerData update(final String cpf, final CustomerData customerData) {
        database.put(cpf, customerData);
        return customerData;
    }

    @Override
    public void delete(final String cpf) {
        database.remove(cpf);
    }

    @Override
    public boolean existsByCpf(final String cpf) {
        return database.containsKey(cpf);
    }
}
