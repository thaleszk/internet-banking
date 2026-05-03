package com.internet.banking.customer.microservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerResponse {

    private String name;
    private String email;
    private String cpf;
    private String phone;
    private BigDecimal salary;
    private AddressResponse address;
}