package com.internet.banking.customer.microservice.exception;


public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(final String message) {
        super(message);
    }
}
