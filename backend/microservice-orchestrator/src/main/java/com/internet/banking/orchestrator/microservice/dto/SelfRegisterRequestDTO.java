package com.internet.banking.orchestrator.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfRegisterRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String cpf;
    private String email;
    private String phone;
    private BigDecimal salary;
    private String password;
    private AddressData address;



}