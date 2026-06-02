package com.internet.banking.orchestrator.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfRegisterRequestDTO {

    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private BigDecimal salario;
    private String senha;
}