package com.internet.banking.orchestrator.microservice.config;

public final class DeleteManagerRabbitConstants {

    private DeleteManagerRabbitConstants() {
    }

    public static final String SAGA_COMMAND_EXCHANGE = "saga.command.exchange";
    public static final String SAGA_EVENT_EXCHANGE = "saga.event.exchange";

    // ===========================
    // Commands routing keys
    // ===========================

    public static final String FIND_REPLACEMENT_MANAGER_COMMAND_ROUTING_KEY =
            "saga.delete-manager.manager.find-replacement.command";

    public static final String TRANSFER_ACCOUNTS_COMMAND_ROUTING_KEY =
            "saga.delete-manager.accounts.transfer.command";

    public static final String DELETE_MANAGER_COMMAND_ROUTING_KEY =
            "saga.delete-manager.manager.delete.command";


    // ===========================
    // Events routing keys
    // ===========================

    public static final String REPLACEMENT_MANAGER_FOUND_EVENT_ROUTING_KEY =
            "saga.delete-manager.manager.replacement-found.event";

    public static final String REPLACEMENT_MANAGER_NOT_FOUND_EVENT_ROUTING_KEY =
            "saga.delete-manager.manager.replacement-not-found.event";

    public static final String ACCOUNTS_TRANSFERRED_EVENT_ROUTING_KEY =
            "saga.delete-manager.account.transferred.event";

    public static final String ACCOUNTS_TRANSFER_FAILED_EVENT_ROUTING_KEY =
            "saga.delete-manager.account.transfer-failed.event";

    public static final String MANAGER_DELETED_EVENT_ROUTING_KEY =
            "saga.delete-manager.manager.deleted.event";

    public static final String MANAGER_DELETION_FAILED_EVENT_ROUTING_KEY =
            "saga.delete-manager.manager.deletion-failed.event";


    // ===========================
// Command queues
// ===========================

    public static final String FIND_REPLACEMENT_MANAGER_COMMAND_QUEUE =
            "manager-service.delete-manager.find-replacement.queue";

    public static final String TRANSFER_ACCOUNTS_COMMAND_QUEUE =
            "account-service.delete-manager.transfer-accounts.queue";

    public static final String DELETE_MANAGER_COMMAND_QUEUE =
            "manager-service.delete-manager.delete-manager.queue";


// ===========================
// Event queues
// ===========================

    public static final String REPLACEMENT_MANAGER_FOUND_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.replacement-manager-found.queue";

    public static final String REPLACEMENT_MANAGER_NOT_FOUND_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.replacement-manager-not-found.queue";

    public static final String ACCOUNTS_TRANSFERRED_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.accounts-transferred.queue";

    public static final String ACCOUNTS_TRANSFER_FAILED_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.accounts-transfer-failed.queue";

    public static final String MANAGER_DELETED_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.manager-deleted.queue";

    public static final String MANAGER_DELETION_FAILED_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.manager-deletion-failed.queue";

}