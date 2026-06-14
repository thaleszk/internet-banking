package com.internet.banking.microservice.auth.service.impl;

import com.internet.banking.microservice.auth.dao.UserRepository;
import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.data.LoginData;
import com.internet.banking.microservice.auth.model.UserModel;
import com.internet.banking.microservice.auth.model.UserType;
import com.internet.banking.microservice.auth.service.AuthService;
import com.internet.banking.microservice.auth.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthService implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public DefaultAuthService(UserRepository userRepository,
                              JwtService jwtService,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthData login(LoginData loginData) {
        UserModel user = userRepository.findByLogin(loginData.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtService.generateToken(user);

        AuthData.UsuarioData usuarioData = new AuthData.UsuarioData();
        usuarioData.setCpf(user.getCpf());
        usuarioData.setNome(user.getNome());
        usuarioData.setEmail(user.getLogin());
        usuarioData.setPerfil(tipoResposta(user).toLowerCase());

        AuthData response = new AuthData();
        response.setAccessToken(token);
        response.setTokenType("bearer");
        response.setTipo(tipoResposta(user));
        response.setUsuario(usuarioData);
        response.setUsername(user.getLogin());
        response.setToken(token);
        response.setType(user.getType().name());

        return response;
    }

    @Override
    public AuthData refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        UserModel user = userRepository.findByLogin(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String newToken = jwtService.generateToken(user);

        AuthData response = new AuthData();
        response.setAccessToken(newToken);
        response.setTokenType("bearer");
        response.setTipo(tipoResposta(user));
        response.setUsername(username);
        response.setToken(newToken);
        response.setType(user.getType().name());

        return response;
    }

    @Override
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }

    // ── Método para a SAGA de Autocadastro criar usuário no auth ─────────────
    @Override
    public UserModel createUser(String cpf, String email, String senha, UserType tipo, String nome) {
        if (userRepository.existsByLogin(email)) {
            throw new RuntimeException("Usuario ja existe com email: " + email);
        }
        UserModel user = new UserModel();
        user.setId(cpf);
        user.setCpf(cpf);
        user.setLogin(email);
        user.setPassword(passwordEncoder.encode(senha));
        user.setType(tipo);
        user.setNome(nome);
        return userRepository.save(user);
    }

    @Override
    public UserModel updateManagerUser(String cpf, String email, String senha, String nome) {
        UserModel user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado para o CPF: " + cpf));

        if (user.getType() != UserType.GERENTE) {
            throw new RuntimeException("Usuario nao e gerente");
        }

        if (email != null && !email.isBlank() && !email.equals(user.getLogin())) {
            userRepository.findByLogin(email)
                    .filter(existing -> !cpf.equals(existing.getCpf()))
                    .ifPresent(existing -> {
                        throw new RuntimeException("Usuario ja existe com email: " + email);
                    });
            user.setLogin(email);
        }

        if (nome != null && !nome.isBlank()) {
            user.setNome(nome);
        }

        if (senha != null && !senha.isBlank()) {
            user.setPassword(passwordEncoder.encode(senha));
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUserByCpf(String cpf) {
        UserModel user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado para o CPF: " + cpf));
        userRepository.delete(user);
    }

    private String tipoResposta(UserModel user) {
        return user.getType().name().equals("ADMIN") ? "ADMINISTRADOR" : user.getType().name();
    }
}
