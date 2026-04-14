package com.internet.banking.microservice.auth.dao.impl;

import com.internet.banking.microservice.auth.model.UserModel;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserDAO implements com.internet.banking.microservice.auth.dao.InMemoryUserDAO {

    private final Map<String, UserModel> userStorage = new HashMap<>();

    @Override
    public UserModel save(UserModel user) {
        userStorage.put(user.getLogin(), user);
        return user;
    }

    @Override
    public Optional<UserModel> findByUsername(String username) {
        return Optional.ofNullable(userStorage.get(username));
    }
}