package com.internet.banking.microservice.auth.config;

import com.internet.banking.microservice.auth.dao.impl.InMemoryUserDAO;
import com.internet.banking.microservice.auth.model.UserModel;
import com.internet.banking.microservice.auth.model.UserType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner inicializarUsuarios(InMemoryUserDAO userDAO, PasswordEncoder passwordEncoder) {
        return args -> {

            // ── Administrador ────────────────────────────────────────────
            salvar(userDAO, passwordEncoder, "adm1@bantads.com.br", "tads", UserType.ADMIN,    "40501740066", "Adamântio");

            // ── Gerentes ─────────────────────────────────────────────────
            salvar(userDAO, passwordEncoder, "ger1@bantads.com.br", "tads", UserType.GERENTE,  "98574307084", "Geniéve");
            salvar(userDAO, passwordEncoder, "ger2@bantads.com.br", "tads", UserType.GERENTE,  "64065268052", "Godophredo");
            salvar(userDAO, passwordEncoder, "ger3@bantads.com.br", "tads", UserType.GERENTE,  "23862179060", "Gyândula");

            // ── Clientes ─────────────────────────────────────────────────
            salvar(userDAO, passwordEncoder, "cli1@bantads.com.br", "tads", UserType.CLIENTE,  "12912861012", "Catharyna");
            salvar(userDAO, passwordEncoder, "cli2@bantads.com.br", "tads", UserType.CLIENTE,  "09506382000", "Cleuddônio");
            salvar(userDAO, passwordEncoder, "cli3@bantads.com.br", "tads", UserType.CLIENTE,  "85733854057", "Catianna");
            salvar(userDAO, passwordEncoder, "cli4@bantads.com.br", "tads", UserType.CLIENTE,  "58872160006", "Cutardo");
            salvar(userDAO, passwordEncoder, "cli5@bantads.com.br", "tads", UserType.CLIENTE,  "76179646090", "Coândrya");

            System.out.println("✅ Usuários pré-cadastrados inicializados com sucesso.");
        };
    }

    private void salvar(InMemoryUserDAO dao, PasswordEncoder encoder,
                        String email, String senha, UserType tipo, String cpf, String nome) {
        UserModel user = new UserModel();
        user.setId(cpf);
        user.setLogin(email);
        user.setPassword(encoder.encode(senha));
        user.setType(tipo);
        user.setCpf(cpf);
        user.setNome(nome);
        dao.save(user);
    }
}