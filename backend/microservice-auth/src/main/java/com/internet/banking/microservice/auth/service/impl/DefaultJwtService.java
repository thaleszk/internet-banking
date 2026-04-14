package com.internet.banking.microservice.auth.service.impl;

import com.internet.banking.microservice.auth.model.UserModel;
import com.internet.banking.microservice.auth.service.JwtService;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class DefaultJwtService implements JwtService {

    @Override
    public String generateToken(UserModel user) {
        String payload = user.getLogin() + ":" + user.getType().name();
        return Base64.getEncoder().encodeToString(payload.getBytes());
    }

    @Override
    public String extractUsername(String token) {
        String decoded = new String(Base64.getDecoder().decode(token));
        return decoded.split(":")[0];
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Base64.getDecoder().decode(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}