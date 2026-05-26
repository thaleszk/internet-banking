package com.internet.banking.orchestrator.microservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteManagerEvent implements Serializable {

    private String cpf;
}