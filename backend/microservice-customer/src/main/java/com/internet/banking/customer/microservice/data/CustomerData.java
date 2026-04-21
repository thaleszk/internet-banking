package com.internet.banking.customer.microservice.data;

import lombok.Data;

@Data
public class CustomerData {

    private String name;
    private String email;
    private String cpf;
    private String phone;
    private String salary;
    private AddressData address;

}
