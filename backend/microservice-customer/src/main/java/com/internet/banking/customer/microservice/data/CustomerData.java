package com.internet.banking.customer.microservice.data;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerData {

    private String name;
    private String email;
    private String cpf;
    private String phone;
    private BigDecimal salary;
    private AddressData address;

}
