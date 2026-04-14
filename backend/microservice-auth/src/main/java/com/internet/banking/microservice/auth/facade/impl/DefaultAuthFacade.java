package com.internet.banking.microservice.auth.facade.impl;

import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.data.LoginData;
import com.internet.banking.microservice.auth.facade.AuthFacade;
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
}