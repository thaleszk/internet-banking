package com.internet.banking.microservice.auth.facade;

import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.data.LoginData;
import com.internet.banking.microservice.auth.model.UserModel;
import com.internet.banking.microservice.auth.enums.UserType;

public interface AuthFacade {

    AuthData login(LoginData authData);

    AuthData refreshToken(String refreshToken);

    boolean validateToken(String token);

    UserModel createUser(String cpf, String email, String senha, UserType tipo, String nome);
}
