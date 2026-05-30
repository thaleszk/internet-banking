package com.internet.banking.microservice.auth.dao;

import com.internet.banking.microservice.auth.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserModel, String> {

    Optional<UserModel> findByLogin(String login);

    Optional<UserModel> findByCpf(String cpf);

    boolean existsByLogin(String login);

    boolean existsByCpf(String cpf);
}