package com.internet.banking.orchestrator.microservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE =
            "manager.exchange";

    public static final String DELETE_QUEUE =
            "manager.delete.requested.queue";

    public static final String ROUTING_KEY =
            "manager.delete.requested";

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(DELETE_QUEUE);
    }

    @Bean
    public Binding binding(
            Queue deleteQueue,
            DirectExchange exchange
    ) {

        return BindingBuilder
                .bind(deleteQueue)
                .to(exchange)
                .with(ROUTING_KEY);
    }
}