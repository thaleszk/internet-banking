package com.internet.banking.microservice_manager.exception;

public class ManagerAlreadyExistsException extends RuntimeException {
    public ManagerAlreadyExistsException(String message) {
        super(message);
    }
}