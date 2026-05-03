package com.internet.banking.customer.microservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank
    private String streetName;

    @NotBlank
    private String streetNumber;

    private String complement;

    @NotBlank
    private String zipCode;

    @NotBlank
    private String city;

    @NotBlank
    private String state;
}