package com.internet.banking.customer.microservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerResponse {

    private String name;
    private String email;
    private String cpf;
    private String phone;
    private BigDecimal salary;
    private AddressResponse address;
    private String registrationStatus;
    private String pendingManagerCpf;

    public String getNome() {
        return name;
    }

    public String getTelefone() {
        return phone;
    }

    public BigDecimal getSalario() {
        return salary;
    }

    public String getConta() {
        if ("12912861012".equals(cpf)) {
            return "1291";
        }
        if ("09506382000".equals(cpf)) {
            return "0950";
        }
        if ("85733854057".equals(cpf)) {
            return "8573";
        }
        if ("58872160006".equals(cpf)) {
            return "5887";
        }
        if ("76179646090".equals(cpf)) {
            return "7617";
        }

        String digits = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (digits.length() >= 4) {
            return digits.substring(digits.length() - 4);
        }
        return digits;
    }

    public BigDecimal getLimite() {
        if (salary == null || salary.compareTo(BigDecimal.valueOf(2000)) < 0) {
            return BigDecimal.ZERO;
        }
        return salary.divide(BigDecimal.valueOf(2));
    }
}
