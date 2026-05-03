package com.internet.banking.customer.microservice.controller;

import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.dto.request.CustomerRequest;
import com.internet.banking.customer.microservice.dto.response.CustomerResponse;
import com.internet.banking.customer.microservice.mapper.CustomerDtoMapper;
import com.internet.banking.customer.microservice.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody final CustomerRequest request) {
        CustomerData createdCustomer = customerService.createCustomer(CustomerDtoMapper.toData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerDtoMapper.toResponse(createdCustomer));
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<CustomerResponse> getCustomerByCpf(@PathVariable final String cpf) {
        CustomerData customer = customerService.getCustomerByCpf(cpf);
        return ResponseEntity.ok(CustomerDtoMapper.toResponse(customer));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerData> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(CustomerDtoMapper.toResponseList(customers));
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable final String cpf,
                                                           @Valid @RequestBody final CustomerRequest request) {
        CustomerData updatedCustomer = customerService.updateCustomer(cpf, CustomerDtoMapper.toData(request));
        return ResponseEntity.ok(CustomerDtoMapper.toResponse(updatedCustomer));
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable final String cpf) {
        customerService.deleteCustomer(cpf);
        return ResponseEntity.noContent().build();
    }
}