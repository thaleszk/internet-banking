package com.internet.banking.microservice_manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "managers")
public class ManagerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column
    private String phone;

    public ManagerModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
    public void setTipo(String tipo) {}
    public void setSenha(String senha) {}
    public void setPassword(String password) {}
}
