package com.internet.banking.microservice.auth.service.impl;

import com.internet.banking.microservice.auth.dao.impl.InMemoryUserDAO;
import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.data.LoginData;
import com.internet.banking.microservice.auth.model.UserModel;
import com.internet.banking.microservice.auth.service.AuthService;
import com.internet.banking.microservice.auth.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthService implements AuthService {

    private final InMemoryUserDAO userDao;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public DefaultAuthService(InMemoryUserDAO userDao,
                              JwtService jwtService,
                              PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthData login(LoginData loginData) {
        UserModel user = userDao.findByUsername(loginData.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!passwordEncoder.matches(loginData.getPassword(), user.getPassword())) {
            throw new RuntimeException("Senha inválida");
        }

        String token = jwtService.generateToken(user);

        // Monta usuario interno
        AuthData.UsuarioData usuarioData = new AuthData.UsuarioData();
        usuarioData.setCpf(user.getCpf());
        usuarioData.setNome(user.getNome());
        usuarioData.setEmail(user.getLogin());
        usuarioData.setPerfil(user.getType().name().toLowerCase()); // "cliente", "gerente", "admin"

        // Monta resposta no formato exigido pelo professor
        AuthData response = new AuthData();
        response.setAccessToken(token);
        response.setTokenType("bearer");
        response.setTipo(user.getType().name()); // "CLIENTE", "GERENTE", "ADMIN"
        response.setUsuario(usuarioData);

        // Campos legados mantidos para compatibilidade
        response.setUsername(user.getLogin());
        response.setToken(token);
        response.setType(user.getType().name());

        return response;
    }

    @Override
    public AuthData refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        UserModel user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String newToken = jwtService.generateToken(user);

        AuthData response = new AuthData();
        response.setAccessToken(newToken);
        response.setTokenType("bearer");
        response.setTipo(user.getType().name());
        response.setUsername(username);
        response.setToken(newToken);
        response.setType(user.getType().name());

        return response;
    }

    @Override
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }
}