package com.internet.banking.microservice.account.config;

public final class DeleteManagerRabbitConstants {

    private DeleteManagerRabbitConstants() {
    }

    // Exchanges
    public static final String SAGA_COMMAND_EXCHANGE =
            "saga.command.exchange";

    public static final String SAGA_EVENT_EXCHANGE =
            "saga.event.exchange";

    // =========================
    // Commands
    // =========================

    public static final String TRANSFER_ACCOUNTS_COMMAND_ROUTING_KEY =
            "saga.delete-manager.account.transfer.command";

    public static final String TRANSFER_ACCOUNTS_COMMAND_QUEUE =
            "account-service.delete-manager.transfer-accounts.queue";

    // =========================
    // Events
    // =========================

    public static final String ACCOUNTS_TRANSFERRED_EVENT_ROUTING_KEY =
            "saga.delete-manager.account.transferred.event";

    public static final String ACCOUNTS_TRANSFER_FAILED_EVENT_ROUTING_KEY =
            "saga.delete-manager.account.transfer-failed.event";

    public static final String ACCOUNTS_TRANSFERRED_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.accounts-transferred.queue";

    public static final String ACCOUNTS_TRANSFER_FAILED_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.accounts-transfer-failed.queue";

}