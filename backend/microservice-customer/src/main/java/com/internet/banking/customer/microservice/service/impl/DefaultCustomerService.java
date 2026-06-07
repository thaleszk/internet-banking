package com.internet.banking.customer.microservice.service.impl;

import com.internet.banking.customer.microservice.dao.CustomerRepository;
import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.exception.CustomerAlreadyExistsException;
import com.internet.banking.customer.microservice.exception.CustomerNotFoundException;
import com.internet.banking.customer.microservice.exception.ProcessingException;
import com.internet.banking.customer.microservice.mapper.AddressMapper;
import com.internet.banking.customer.microservice.mapper.CustomerMapper;
import com.internet.banking.customer.microservice.model.CustomerModel;
import com.internet.banking.customer.microservice.model.RegistrationStatus;
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
                    "Cliente ja cadastrado para o CPF: " + customerData.getCpf()
            );
        }

        CustomerModel customerModel = CustomerMapper.toModel(customerData);
        if (Objects.isNull(customerModel)) {
            throw new ProcessingException("Erro ao criar os dados do cliente");
        }
        customerModel.setRegistrationStatus(RegistrationStatus.ACTIVE);
        customerModel.setPendingManagerCpf(null);
        CustomerModel savedCustomer = repository.save(customerModel);

        return CustomerMapper.toData(savedCustomer);
    }

    @Override
    public CustomerData getCustomerByCpf(final String cpf) {
        CustomerModel customerModel = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

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
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

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
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

        repository.delete(existingCustomer);
    }

    @Override
    public List<CustomerData> listPendingRegistration() {
        return repository.findByRegistrationStatusOrderByNameAsc(RegistrationStatus.PENDING_APPROVAL)
                .stream()
                .map(CustomerMapper::toData)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerData createPendingRegistration(final CustomerData customerData) {
        validateCustomer(customerData);
        if (repository.existsByCpf(customerData.getCpf())) {
            throw new CustomerAlreadyExistsException(
                    "CPF ja cadastrado ou ja existe solicitacao pendente para: " + customerData.getCpf()
            );
        }
        CustomerModel model = CustomerMapper.toModel(customerData);
        if (Objects.isNull(model)) {
            throw new ProcessingException("Erro ao criar os dados do cliente");
        }
        model.setRegistrationStatus(RegistrationStatus.PENDING_APPROVAL);
        model.setPendingManagerCpf(null);
        CustomerModel saved = repository.save(model);
        return CustomerMapper.toData(saved);
    }

    @Override
    public CustomerData approveRegistration(final String cpf) {
        CustomerModel customerModel = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

        customerModel.setRegistrationStatus(RegistrationStatus.ACTIVE);
        customerModel.setPendingManagerCpf(null);

        return CustomerMapper.toData(repository.save(customerModel));
    }

    @Override
    public void rejectRegistration(final String cpf) {
        CustomerModel customerModel = repository.findByCpf(cpf)
                .orElseThrow(() -> new CustomerNotFoundException("Cliente nao encontrado para o CPF: " + cpf));

        repository.delete(customerModel);
    }

    private void validateCustomer(final CustomerData customerData) {
        if (customerData == null) {
            throw new IllegalArgumentException("Cliente nao pode ser nulo");
        }

        if (customerData.getCpf() == null || customerData.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF do cliente e obrigatorio");
        }

        if (customerData.getAddress() == null) {
            throw new IllegalArgumentException("Endereco do cliente e obrigatorio");
        }
    }
}
