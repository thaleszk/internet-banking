package com.internet.banking.customer.microservice.service.impl;
import com.internet.banking.customer.microservice.dao.CustomerRepository;
import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.exception.CustomerAlreadyExistsException;
import com.internet.banking.customer.microservice.exception.CustomerNotFoundException;
import com.internet.banking.customer.microservice.exception.ProcessingException;
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
    public CustomerModel getCustomerByCpf(final String cpf) {
        CustomerModel customerModel = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for CPF: " + cpf));

        return CustomerMapper.toModel(customerData);
    }

    @Override
    public List<CustomerModel> getAllCustomers() {
        return repository.findAll()
                .stream()
                .map(CustomerMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerModel updateCustomer(final String cpf, final CustomerModel customerModel) {
        validateCustomerModel(customerModel);

        if (!repository.existsByCpf(cpf)) {
            throw new CustomerNotFoundException("Customer not found for CPF: " + cpf);
        }

        customerModel.setCpf(cpf);

        CustomerData customerData = CustomerMapper.toData(customerModel);
        CustomerData updatedCustomer = repository.update(cpf, customerData);

        return CustomerMapper.toModel(updatedCustomer);
    }

    @Override
    public void deleteCustomer(final String cpf) {
        if (!repository.existsByCpf(cpf)) {
            throw new CustomerNotFoundException("Customer not found for CPF: " + cpf);
        }

        repository.delete(cpf);
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
