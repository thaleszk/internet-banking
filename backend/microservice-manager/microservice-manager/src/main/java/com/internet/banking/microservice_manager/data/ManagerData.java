package com.internet.banking.microservice_manager.data;

public class ManagerData {

    private String name;
    private String email;
    private String cpf;
    private String phone;

    public ManagerData() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNome() { return name; }
    public void setNome(String nome) { this.name = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getTelefone() { return phone; }
    public void setTelefone(String telefone) { this.phone = telefone; }

    public String getTipo() { return "GERENTE"; }
}
