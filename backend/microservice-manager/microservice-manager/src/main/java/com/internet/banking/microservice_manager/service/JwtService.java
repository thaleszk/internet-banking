package com.internet.banking.microservice.auth.service;

import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.model.UserModel;

public interface JwtService {
    String generateToken(UserModel user);
    String extractUsername(String token);
    boolean validateToken(String token);
}
