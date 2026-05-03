package com.internet.banking.customer.microservice.dto.response;

import lombok.Data;

@Data
public class AddressResponse {

    private String streetName;
    private String streetNumber;
    private String complement;
    private String zipCode;
    private String city;
    private String state;
}