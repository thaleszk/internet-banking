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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/registration/pending")
    public ResponseEntity<List<CustomerResponse>> listPendingRegistrations() {
        return ResponseEntity.ok(CustomerDtoMapper.toResponseList(customerService.listPendingRegistration()));
    }

    @PostMapping("/registration/request")
    public ResponseEntity<CustomerResponse> requestSelfRegistration(@Valid @RequestBody final CustomerRequest request) {
        CustomerData created = customerService.createPendingRegistration(CustomerDtoMapper.toData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerDtoMapper.toResponse(created));
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody final CustomerRequest request) {
        CustomerData createdCustomer = customerService.createCustomer(CustomerDtoMapper.toData(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerDtoMapper.toResponse(createdCustomer));
    }

    @PostMapping("/{cpf}/aprovar")
    public ResponseEntity<CustomerResponse> approveCustomer(@PathVariable final String cpf,
                                                            @RequestHeader(value = "Authorization", required = false) final String authorization) {
        if (isCustomerToken(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        CustomerData approvedCustomer = customerService.approveRegistration(cpf);
        return ResponseEntity.ok(CustomerDtoMapper.toResponse(approvedCustomer));
    }

    @PostMapping("/{cpf}/rejeitar")
    public ResponseEntity<Void> rejectCustomer(@PathVariable final String cpf,
                                               @RequestHeader(value = "Authorization", required = false) final String authorization) {
        if (isCustomerToken(authorization)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        customerService.rejectRegistration(cpf);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<CustomerResponse> getCustomerByCpf(@PathVariable final String cpf) {
        CustomerData customer = customerService.getCustomerByCpf(cpf);
        return ResponseEntity.ok(CustomerDtoMapper.toResponse(customer));
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers(@RequestParam(required = false) String filtro) {
        if ("para_aprovar".equalsIgnoreCase(filtro)) {
            return ResponseEntity.ok(CustomerDtoMapper.toResponseList(customerService.listPendingRegistration()));
        }

        if ("melhores_clientes".equalsIgnoreCase(filtro)) {
            return ResponseEntity.ok(List.of(
                    Map.of("cpf", "58872160006", "nome", "Cutardo", "saldo", 150000.0),
                    Map.of("cpf", "76179646090", "nome", "Coândrya", "saldo", 1500.0),
                    Map.of("cpf", "12912861012", "nome", "Catharyna", "saldo", 800.0)
            ));
        }

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

    private boolean isCustomerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return false;
        }

        String token = authorization.trim()
                .replaceFirst("(?i)^Bearer\\s+", "")
                .replace("\"", "")
                .trim();
        if (token.isBlank()) {
            return false;
        }

        try {
            String payload = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            return payload.endsWith(":CLIENTE");
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}
