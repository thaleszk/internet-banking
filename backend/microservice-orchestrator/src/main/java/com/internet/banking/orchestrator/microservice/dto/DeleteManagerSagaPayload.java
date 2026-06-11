package com.internet.banking.orchestrator.microservice.dto;

import lombok.Data;

@Data
public class DeleteManagerSagaPayload {

    public DeleteManagerSagaPayload(
            final String cpf
    ) {
        this.cpf = cpf;
    }

    private String cpf;

    private Long managerId;

    private Long replacementManagerId;

    private Integer transferredCustomers;

}
