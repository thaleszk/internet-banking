package com.internet.banking.microservice_manager.config;

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

    public static final String FIND_REPLACEMENT_MANAGER_COMMAND_ROUTING_KEY =
            "saga.delete-manager.manager.find-replacement.command";

    public static final String TRANSFER_CUSTOMERS_COMMAND_ROUTING_KEY =
            "saga.delete-manager.customer.transfer.command";

    public static final String DELETE_MANAGER_COMMAND_ROUTING_KEY =
            "saga.delete-manager.manager.delete.command";

    // =========================
    // Events
    // =========================

    public static final String REPLACEMENT_MANAGER_FOUND_EVENT_ROUTING_KEY =
            "saga.delete-manager.manager.replacement-found.event";

    public static final String REPLACEMENT_MANAGER_NOT_FOUND_EVENT_ROUTING_KEY =
            "saga.delete-manager.manager.replacement-not-found.event";

    public static final String CUSTOMERS_TRANSFERRED_EVENT_ROUTING_KEY =
            "saga.delete-manager.customer.transferred.event";

    public static final String CUSTOMER_TRANSFER_FAILED_EVENT_ROUTING_KEY =
            "saga.delete-manager.customer.transfer-failed.event";

    public static final String MANAGER_DELETED_EVENT_ROUTING_KEY =
            "saga.delete-manager.manager.deleted.event";

    public static final String MANAGER_DELETION_FAILED_EVENT_ROUTING_KEY =
            "saga.delete-manager.manager.deletion-failed.event";

    // =========================
    // Queues
    // =========================

    public static final String FIND_REPLACEMENT_MANAGER_COMMAND_QUEUE =
            "manager-service.delete-manager.find-replacement.queue";

    public static final String REPLACEMENT_MANAGER_FOUND_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.replacement-manager-found.queue";

    public static final String REPLACEMENT_MANAGER_NOT_FOUND_EVENT_QUEUE =
            "saga-orchestrator.delete-manager.replacement-manager-not-found.queue";

}