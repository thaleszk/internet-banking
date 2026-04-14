package com.internet.banking.microservice.auth.dao;

import com.internet.banking.microservice.auth.model.UserModel;

import java.util.Optional;

public interface InMemoryUserDAO {

    UserModel save(UserModel user);

    Optional<UserModel> findByUsername(String username);
}