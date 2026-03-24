package com.internet.banking.customer.microservice.controller;

import com.internet.banking.customer.microservice.facade.CustomerFacade;
import com.internet.banking.customer.microservice.model.CustomerModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerFacade customerFacade;

    public CustomerController(final CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    @PostMapping
    public ResponseEntity<CustomerModel> createCustomer(@RequestBody final CustomerModel customerModel) {
        CustomerModel createdCustomer = customerFacade.createCustomer(customerModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<CustomerModel> getCustomerByCpf(@PathVariable final String cpf) {
        CustomerModel customer = customerFacade.getCustomerByCpf(cpf);
        return ResponseEntity.ok(customer);
    }

    @GetMapping
    public ResponseEntity<List<CustomerModel>> getAllCustomers() {
        List<CustomerModel> customers = customerFacade.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<CustomerModel> updateCustomer(@PathVariable final String cpf,
                                                        @RequestBody final CustomerModel customerModel) {
        CustomerModel updatedCustomer = customerFacade.updateCustomer(cpf, customerModel);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable final String cpf) {
        customerFacade.deleteCustomer(cpf);
        return ResponseEntity.noContent().build();
    }
}
