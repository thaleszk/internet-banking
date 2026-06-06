package com.internet.banking.microservice.auth.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        String uri = System.getenv().getOrDefault(
                "SPRING_DATA_MONGODB_URI",
                "mongodb://auth-mongo:27017/authdb"
        );
        return MongoClients.create(uri);
    }
}
