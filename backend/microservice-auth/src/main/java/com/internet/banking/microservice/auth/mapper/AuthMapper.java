package com.internet.banking.microservice.auth.mapper;

import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.model.UserModel;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthData toData(UserModel userModel, String token) {
        if (userModel == null) {
            return null;
        }

        AuthData authData = new AuthData();
        authData.setUsername(userModel.getLogin());
        authData.setToken(token);
        authData.setType(userModel.getType().name());

        return authData;
    }
}