package com.internet.banking.orchestrator.microservice.dto;

import lombok.Data;

@Data
public class CustomerSelfRegistrationSagaPayload {

    private String name;
    private String cpf;
    private String email;
    private String phone;

    private Long customerId;
    private Long managerId;
    private Long accountId;
    private Long authenticationId;

    public CustomerSelfRegistrationSagaPayload() {
    }

    public CustomerSelfRegistrationSagaPayload(
            final String name,
            final String cpf,
            final String email,
            final String phone
    ) {
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
    }
}
