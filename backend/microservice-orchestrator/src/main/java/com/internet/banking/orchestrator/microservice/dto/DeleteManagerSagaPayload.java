package com.internet.banking.orchestrator.microservice.dto;

import lombok.Data;

@Data
public class DeleteManagerSagaPayload {

    private String cpf;

    private Long managerId;

    private Long replacementManagerId;

    private String currentManagerCpf;

    private String replacementManagerCpf;

    private Integer transferredAccounts;

    public DeleteManagerSagaPayload(
            final String cpf
    ) {
        this.cpf = cpf;
        this.currentManagerCpf = cpf;
    }

    public DeleteManagerSagaPayload() {
    }

}