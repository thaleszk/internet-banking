package com.internet.banking.microservice.auth.facade;

import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.data.LoginData;

public interface AuthFacade {

    AuthData login(LoginData authData);

    AuthData refreshToken(String refreshToken);

    boolean validateToken(String token);
}