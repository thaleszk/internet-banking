package com.internet.banking.customer.microservice.facade.impl;

import com.internet.banking.customer.microservice.facade.CustomerFacade;
import com.internet.banking.customer.microservice.model.CustomerModel;
import com.internet.banking.customer.microservice.service.CustomerService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultCustomerFacade implements CustomerFacade {

    private final CustomerService customerService;

    public DefaultCustomerFacade(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public CustomerModel createCustomer(final CustomerModel customerModel) {
        return customerService.createCustomer(customerModel);
    }

    @Override
    public CustomerModel getCustomerByCpf(final String cpf) {
        return customerService.getCustomerByCpf(cpf);
    }

    @Override
    public List<CustomerModel> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Override
    public CustomerModel updateCustomer(final String cpf, final CustomerModel customerModel) {
        return customerService.updateCustomer(cpf, customerModel);
    }

    @Override
    public void deleteCustomer(final String cpf) {
        customerService.deleteCustomer(cpf);
    }
}
