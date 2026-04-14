package com.internet.banking.microservice.auth.service;

import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.data.LoginData;

public interface AuthService {

    AuthData login(LoginData loginData);

    AuthData refreshToken(String refreshToken);

    boolean validateToken(String token);
}