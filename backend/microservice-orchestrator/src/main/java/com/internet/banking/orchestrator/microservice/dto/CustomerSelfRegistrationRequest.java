package com.internet.banking.orchestrator.microservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CustomerSelfRegistrationRequest(

        @NotBlank
        @JsonAlias("nome")
        String name,

        @NotBlank
        String cpf,

        @NotBlank
        @Email
        String email,

        @JsonAlias("telefone")
        String phone,

        @JsonAlias("salario")
        BigDecimal salary,

        @JsonAlias("endereco")
        String address,

        @JsonProperty("CEP")
        String cep,

        @JsonAlias("cidade")
        String city,

        @JsonAlias("estado")
        String state
) {
}
