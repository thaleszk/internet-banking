package com.internet.banking.microservice.account.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountCqrsRabbitConfig {

    @Bean
    public DirectExchange accountCqrsExchange() {
        return new DirectExchange(AccountCqrsRabbitConstants.ACCOUNT_CQRS_EXCHANGE);
    }

    @Bean
    public Queue accountProjectionQueue() {
        return new Queue(AccountCqrsRabbitConstants.ACCOUNT_PROJECTION_QUEUE);
    }

    @Bean
    public Queue transactionProjectionQueue() {
        return new Queue(AccountCqrsRabbitConstants.TRANSACTION_PROJECTION_QUEUE);
    }

    @Bean
    public Binding accountProjectionBinding(
            Queue accountProjectionQueue,
            DirectExchange accountCqrsExchange
    ) {
        return BindingBuilder
                .bind(accountProjectionQueue)
                .to(accountCqrsExchange)
                .with(AccountCqrsRabbitConstants.ACCOUNT_PROJECTION_ROUTING_KEY);
    }

    @Bean
    public Binding transactionProjectionBinding(
            Queue transactionProjectionQueue,
            DirectExchange accountCqrsExchange
    ) {
        return BindingBuilder
                .bind(transactionProjectionQueue)
                .to(accountCqrsExchange)
                .with(AccountCqrsRabbitConstants.TRANSACTION_PROJECTION_ROUTING_KEY);
    }
}
