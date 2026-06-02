package com.internet.banking.orchestrator.microservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfigSelfDeclaration {

    @Bean
    public Queue customerCreatedQueue() {
        return new Queue(
                "customer.created.queue",
                true
        );
    }
    @Bean
    public Queue customerCreateQueue() {
        return new Queue(
                "customer.create.queue",
                true
        );
    }

    @Bean
    public Queue authCreateQueue() {
        return new Queue(
                "auth.create.queue",
                true
        );
    }

    @Bean
    public Queue authFailedQueue() {
        return new Queue(
                "auth.failed.queue",
                true
        );
    }

    @Bean
    public Queue customerRollbackQueue() {
        return new Queue(
                "customer.rollback.queue",
                true
        );
    }
}