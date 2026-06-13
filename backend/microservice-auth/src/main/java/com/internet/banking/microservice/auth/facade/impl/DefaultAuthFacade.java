package com.internet.banking.microservice.auth.facade.impl;

import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.data.LoginData;
import com.internet.banking.microservice.auth.facade.AuthFacade;
import com.internet.banking.microservice.auth.model.UserModel;
import com.internet.banking.microservice.auth.model.UserType;
import com.internet.banking.microservice.auth.service.AuthService;
import org.springframework.stereotype.Component;

@Component
public class DefaultAuthFacade implements AuthFacade {

    private final AuthService authService;

    public DefaultAuthFacade(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public AuthData login(LoginData loginData) {
        return authService.login(loginData);
    }

    @Override
    public AuthData refreshToken(String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @Override
    public boolean validateToken(String token) {
        return authService.validateToken(token);
    }

    @Override
    public UserModel createUser(String cpf, String email, String senha, UserType tipo, String nome) {
        return authService.createUser(cpf, email, senha, tipo, nome);
    }

    @Override
    public UserModel updateManagerUser(String cpf, String email, String senha, String nome) {
        return authService.updateManagerUser(cpf, email, senha, nome);
    }

    @Override
    public void deleteUserByCpf(String cpf) {
        authService.deleteUserByCpf(cpf);
    }
}
