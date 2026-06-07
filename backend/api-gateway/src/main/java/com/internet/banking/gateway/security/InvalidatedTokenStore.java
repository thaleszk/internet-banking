package com.internet.banking.gateway.security;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class InvalidatedTokenStore {

    private final Set<String> tokens = ConcurrentHashMap.newKeySet();

    public void add(String token) {
        if (token != null && !token.isBlank()) {
            tokens.add(token);
        }
    }

    public boolean contains(String token) {
        return token != null && tokens.contains(token);
    }
}
