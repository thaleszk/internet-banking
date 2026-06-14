package com.internet.banking.microservice.auth.config;

import com.internet.banking.microservice.auth.dao.UserRepository;
import com.internet.banking.microservice.auth.model.UserModel;
import com.internet.banking.microservice.auth.model.UserType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner inicializarUsuarios(UserRepository userRepository,
                                                  PasswordEncoder passwordEncoder) {
        return args -> {
            // Só insere se o banco estiver vazio (evita duplicatas em restart)
            if (userRepository.count() > 0) {
                System.out.println("✅ Usuários já existem no MongoDB — inicialização ignorada.");
                return;
            }

            // ── Administrador ──────────────────────────────────────────────
            salvar(userRepository, passwordEncoder,
                    "adm1@bantads.com.br", "tads", UserType.ADMIN, "40501740066", "Adamântio");

            // ── Gerentes ───────────────────────────────────────────────────
            salvar(userRepository, passwordEncoder,
                    "ger1@bantads.com.br", "tads", UserType.GERENTE, "98574307084", "Geniéve");
            salvar(userRepository, passwordEncoder,
                    "ger2@bantads.com.br", "tads", UserType.GERENTE, "64065268052", "Godophredo");
            salvar(userRepository, passwordEncoder,
                    "ger3@bantads.com.br", "tads", UserType.GERENTE, "23862179060", "Gyândula");

            // ── Clientes ───────────────────────────────────────────────────
            salvar(userRepository, passwordEncoder,
                    "cli1@bantads.com.br", "tads", UserType.CLIENTE, "12912861012", "Catharyna");
            salvar(userRepository, passwordEncoder,
                    "cli2@bantads.com.br", "tads", UserType.CLIENTE, "09506382000", "Cleuddônio");
            salvar(userRepository, passwordEncoder,
                    "cli3@bantads.com.br", "tads", UserType.CLIENTE, "85733854057", "Catianna");
            salvar(userRepository, passwordEncoder,
                    "cli4@bantads.com.br", "tads", UserType.CLIENTE, "58872160006", "Cutardo");
            salvar(userRepository, passwordEncoder,
                    "cli5@bantads.com.br", "tads", UserType.CLIENTE, "76179646090", "Coândrya");

            System.out.println("✅ Usuários pré-cadastrados inseridos no MongoDB com sucesso.");
        };
    }

    private void salvar(UserRepository repository, PasswordEncoder encoder,
                        String email, String senha, UserType tipo, String cpf, String nome) {
        UserModel user = new UserModel();
        user.setId(cpf);
        user.setLogin(email);
        user.setPassword(encoder.encode(senha));
        user.setType(tipo);
        user.setCpf(cpf);
        user.setNome(nome);
        repository.save(user);
    }
}