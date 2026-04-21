package com.internet.banking.customer.microservice.data;

import lombok.Data;

@Data
public class AddressData {

    private String streetName;
    private String streetNumber;
    private String complement;
    private String zipCode;
    private String city;
    private String state;
}
