package com.internet.banking.customer.microservice.facade;

import com.internet.banking.customer.microservice.model.CustomerModel;

import java.util.List;

public interface CustomerFacade {

    CustomerModel createCustomer(CustomerModel customerModel);

    CustomerModel getCustomerByCpf(String cpf);

    List<CustomerModel> getAllCustomers();

    CustomerModel updateCustomer(String cpf, CustomerModel customerModel);

    void deleteCustomer(String cpf);
}
