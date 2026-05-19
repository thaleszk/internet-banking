package com.internet.banking.microservice_manager.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE =
            "manager.exchange";

    public static final String DELETE_REQUEST_QUEUE =
            "manager.delete.requested.queue";

    public static final String DELETE_VALIDATED_QUEUE =
            "manager.delete.validated.queue";

    public static final String DELETE_FAILED_QUEUE =
            "manager.delete.failed.queue";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue deleteRequestQueue() {
        return new Queue(DELETE_REQUEST_QUEUE);
    }

    @Bean
    public Queue validatedQueue() {
        return new Queue(DELETE_VALIDATED_QUEUE);
    }

    @Bean
    public Queue failedQueue() {
        return new Queue(DELETE_FAILED_QUEUE);
    }

    @Bean
    public Binding deleteRequestBinding() {

        return BindingBuilder
                .bind(deleteRequestQueue())
                .to(exchange())
                .with("manager.delete.requested");
    }

    @Bean
    public Binding validatedBinding() {

        return BindingBuilder
                .bind(validatedQueue())
                .to(exchange())
                .with("manager.delete.validated");
    }

    @Bean
    public Binding failedBinding() {

        return BindingBuilder
                .bind(failedQueue())
                .to(exchange())
                .with("manager.delete.failed");
    }
}