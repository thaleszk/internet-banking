package com.internet.banking.customer.microservice.service;

import com.internet.banking.customer.microservice.data.CustomerData;

import java.util.List;

public interface CustomerService {

    CustomerData createCustomer(CustomerData customerData);

    CustomerData getCustomerByCpf(String cpf);

    List<CustomerData> getAllCustomers();

    CustomerData updateCustomer(String cpf, CustomerData customerData);

    void deleteCustomer(String cpf);
}
