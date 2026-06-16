package com.internet.banking.microservice.account.config;

public final class AccountCqrsRabbitConstants {

    private AccountCqrsRabbitConstants() {
    }

    public static final String ACCOUNT_CQRS_EXCHANGE =
            "account.cqrs.exchange";

    public static final String ACCOUNT_PROJECTION_QUEUE =
            "account.cqrs.account-projection.queue";

    public static final String TRANSACTION_PROJECTION_QUEUE =
            "account.cqrs.transaction-projection.queue";

    public static final String ACCOUNT_PROJECTION_ROUTING_KEY =
            "account.cqrs.account.projection";

    public static final String TRANSACTION_PROJECTION_ROUTING_KEY =
            "account.cqrs.transaction.projection";
}
