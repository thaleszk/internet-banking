package com.internet.banking.customer.microservice.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRequest {

    @NotBlank
    @JsonAlias("nome")
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String cpf;

    @JsonAlias("telefone")
    private String phone;

    @JsonAlias("salario")
    private BigDecimal salary;

    @Valid
    private AddressRequest address;

    @JsonAlias({"endereco", "logradouro"})
    private String streetName;

    @JsonAlias({"numero", "número"})
    private String streetNumber;

    @JsonAlias("complemento")
    private String complement;

    @JsonAlias({"CEP", "cep"})
    private String zipCode;

    @JsonAlias("cidade")
    private String city;

    @JsonAlias("estado")
    private String state;
}
