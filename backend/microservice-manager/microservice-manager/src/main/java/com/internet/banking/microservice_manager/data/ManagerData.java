package com.internet.banking.manager.microservice.data;

public class ManagerData {

    private String name;
    private String email;
    private String cpf;
    private String phone;

    public ManagerData() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}