package com.internet.banking.customer.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreatedEvent {

    private Long customerId;

    private String cpf;

    private String nome;

    private String email;

    private String senha;

    private String sagaId;

    public CustomerCreatedEvent(long id, String cpf, String email) {
    }
}