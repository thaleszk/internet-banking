package com.internet.banking.customer.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaErrorEvent {

    private String sagaId;

    private String service;

    private String step;

    private String cpf;

    private String message;
}