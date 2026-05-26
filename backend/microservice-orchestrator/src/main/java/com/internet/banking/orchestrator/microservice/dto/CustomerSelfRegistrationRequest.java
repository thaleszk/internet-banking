package com.internet.banking.orchestrator.microservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerSelfRegistrationRequest(

        @NotBlank
        String name,

        @NotBlank
        String cpf,

        @NotBlank
        @Email
        String email,

        String phone
) {
}
