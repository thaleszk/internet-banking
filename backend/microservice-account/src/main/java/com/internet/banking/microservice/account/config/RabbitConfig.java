package com.internet.banking.microservice.account.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE =
            "manager.exchange";

    public static final String VALIDATED_QUEUE =
            "manager.delete.validated.queue";

    public static final String TRANSFER_COMPLETED_QUEUE =
            "account.transfer.completed.queue";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue validatedQueue() {
        return new Queue(VALIDATED_QUEUE);
    }

    @Bean
    public Queue completedQueue() {
        return new Queue(TRANSFER_COMPLETED_QUEUE);
    }

    @Bean
    public Binding validatedBinding() {

        return BindingBuilder
                .bind(validatedQueue())
                .to(exchange())
                .with("manager.delete.validated");
    }

    @Bean
    public Binding completedBinding() {

        return BindingBuilder
                .bind(completedQueue())
                .to(exchange())
                .with("account.transfer.completed");
    }
}