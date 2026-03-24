package com.internet.banking.customer.microservice.service.impl;
import com.internet.banking.customer.microservice.dao.CustomerDao;
import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.exception.CustomerAlreadyExistsException;
import com.internet.banking.customer.microservice.exception.CustomerNotFoundException;
import com.internet.banking.customer.microservice.mapper.CustomerMapper;
import com.internet.banking.customer.microservice.model.CustomerModel;
import com.internet.banking.customer.microservice.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultCustomerService implements CustomerService {

    private final CustomerDao customerDao;

    public DefaultCustomerService(final CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public CustomerModel createCustomer(final CustomerModel customerModel) {
        validateCustomerModel(customerModel);

        if (customerDao.existsByCpf(customerModel.getCpf())) {
            throw new CustomerAlreadyExistsException(
                    "Customer already exists for CPF: " + customerModel.getCpf()
            );
        }

        CustomerData customerData = CustomerMapper.toData(customerModel);
        CustomerData savedCustomer = customerDao.save(customerData);

        return CustomerMapper.toModel(savedCustomer);
    }

    @Override
    public CustomerModel getCustomerByCpf(final String cpf) {
        CustomerData customerData = customerDao.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found for CPF: " + cpf));

        return CustomerMapper.toModel(customerData);
    }

    @Override
    public List<CustomerModel> getAllCustomers() {
        return customerDao.findAll()
                .stream()
                .map(CustomerMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerModel updateCustomer(final String cpf, final CustomerModel customerModel) {
        validateCustomerModel(customerModel);

        if (!customerDao.existsByCpf(cpf)) {
            throw new CustomerNotFoundException("Customer not found for CPF: " + cpf);
        }

        customerModel.setCpf(cpf);

        CustomerData customerData = CustomerMapper.toData(customerModel);
        CustomerData updatedCustomer = customerDao.update(cpf, customerData);

        return CustomerMapper.toModel(updatedCustomer);
    }

    @Override
    public void deleteCustomer(final String cpf) {
        if (!customerDao.existsByCpf(cpf)) {
            throw new CustomerNotFoundException("Customer not found for CPF: " + cpf);
        }

        customerDao.delete(cpf);
    }

    private void validateCustomerModel(final CustomerModel customerModel) {
        if (customerModel == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }

        if (customerModel.getCpf() == null || customerModel.getCpf().isBlank()) {
            throw new IllegalArgumentException("Customer CPF must not be null or blank");
        }

        if (customerModel.getAddress() == null) {
            throw new IllegalArgumentException("Customer address must not be null");
        }
    }
}
