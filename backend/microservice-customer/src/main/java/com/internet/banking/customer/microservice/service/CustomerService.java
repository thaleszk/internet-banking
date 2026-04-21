package com.internet.banking.customer.microservice.service;

import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.model.CustomerModel;

import java.util.List;

public interface CustomerService {

    CustomerData createCustomer(CustomerData customerData);

    CustomerModel getCustomerByCpf(String cpf);

    List<CustomerModel> getAllCustomers();

    CustomerModel updateCustomer(String cpf, CustomerModel customerModel);

    void deleteCustomer(String cpf);
}
