package com.internet.banking.customer.microservice.service.impl;
import com.internet.banking.customer.microservice.dao.CustomerRepository;
import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.exception.CustomerAlreadyExistsException;
import com.internet.banking.customer.microservice.exception.CustomerNotFoundException;
import com.internet.banking.customer.microservice.exception.ProcessingException;
import com.internet.banking.customer.microservice.mapper.AddressMapper;
import com.internet.banking.customer.microservice.mapper.CustomerMapper;
import com.internet.banking.customer.microservice.model.CustomerModel;
import com.internet.banking.customer.microservice.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DefaultCustomerService implements CustomerService {

    private final CustomerRepository repository;

    public DefaultCustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public CustomerData createCustomer(final CustomerData customerData) {
        validateCustomer(customerData);

        if (repository.existsByCpf(customerData.getCpf())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists for CPF: " + customerData.getCpf()
            );
        }

        CustomerModel customerModel = CustomerMapper.toModel(customerData);
        if (Objects.isNull(customerModel)) {
            throw new ProcessingException("Error while creating customer model");
        }
        CustomerModel savedCustomer = repository.save(customerModel);

        return CustomerMapper.toData(savedCustomer);
    }

    @Override
    public CustomerData getCustomerByCpf(final String cpf) {
        CustomerModel customerModel = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for CPF: " + cpf));

        return CustomerMapper.toData(customerModel);
    }

    @Override
    public List<CustomerData> getAllCustomers() {
        return repository.findAll()
                .stream()
                .map(CustomerMapper::toData)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerData updateCustomer(final String cpf, final CustomerData customerData) {
        validateCustomer(customerData);

        CustomerModel existingCustomer = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for CPF: " + cpf));

        existingCustomer.setName(customerData.getName());
        existingCustomer.setEmail(customerData.getEmail());
        existingCustomer.setPhone(customerData.getPhone());
        existingCustomer.setSalary(customerData.getSalary());
        existingCustomer.setAddress(AddressMapper.toModel(customerData.getAddress()));

        CustomerModel savedCustomer = repository.save(existingCustomer);

        return CustomerMapper.toData(savedCustomer);
    }

    @Override
    public void deleteCustomer(final String cpf) {
        CustomerModel existingCustomer = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for CPF: " + cpf));

        repository.delete(existingCustomer);
    }

    private void validateCustomer(final CustomerData customerData) {
        if (customerData == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }

        if (customerData.getCpf() == null || customerData.getCpf().isBlank()) {
            throw new IllegalArgumentException("Customer CPF must not be null or blank");
        }

        if (customerData.getAddress() == null) {
            throw new IllegalArgumentException("Customer address must not be null");
        }
    }
}
