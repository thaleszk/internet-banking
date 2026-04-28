package com.internet.banking.customer.microservice.controller;

import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.model.CustomerModel;
import com.internet.banking.customer.microservice.service.CustomerService;
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
    public ResponseEntity<CustomerData> createCustomer(@RequestBody final CustomerData customerData) {
        CustomerData createdCustomer = customerService.createCustomer(customerData);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<CustomerData> getCustomerByCpf(@PathVariable final String cpf) {
        CustomerData customer = customerService.getCustomerByCpf(cpf);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<List<CustomerData>> getAllCustomers() {
        List<CustomerData> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<CustomerData> updateCustomer(@PathVariable final String cpf,
                                                        @RequestBody final CustomerData customerData) {
        CustomerData updatedCustomer = customerService.updateCustomer(cpf, customerData);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable final String cpf) {
        customerService.deleteCustomer(cpf);
        return ResponseEntity.noContent().build();
    }
}
