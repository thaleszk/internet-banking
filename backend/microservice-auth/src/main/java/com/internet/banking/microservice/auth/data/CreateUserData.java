package com.internet.banking.microservice.auth.data;

public record CreateUserData(
        String cpf,
        String email,
        String senha,
        String nome
) {
}
