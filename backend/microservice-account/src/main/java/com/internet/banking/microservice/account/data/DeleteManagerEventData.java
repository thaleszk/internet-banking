package com.internet.banking.microservice.account.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteManagerEventData implements Serializable {

    private String cpf;
}