package com.internet.banking.orchestrator.microservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internet.banking.orchestrator.microservice.command.deleteManager.DeleteManagerCommand;
import com.internet.banking.orchestrator.microservice.command.deleteManager.FindReplacementManagerCommand;
import com.internet.banking.orchestrator.microservice.command.deleteManager.TransferCustomersCommand;
import com.internet.banking.orchestrator.microservice.config.DeleteManagerRabbitConstants;
import com.internet.banking.orchestrator.microservice.dto.DeleteManagerRequest;
import com.internet.banking.orchestrator.microservice.dto.DeleteManagerSagaPayload;
import com.internet.banking.orchestrator.microservice.enums.DeleteManagerSagaStatus;
import com.internet.banking.orchestrator.microservice.enums.DeleteManagerSagaStep;
import com.internet.banking.orchestrator.microservice.enums.SagaType;
import com.internet.banking.orchestrator.microservice.event.deleteManager.*;
import com.internet.banking.orchestrator.microservice.model.SagaInstanceModel;
import com.internet.banking.orchestrator.microservice.repository.SagaInstanceRepository;
import com.internet.banking.orchestrator.microservice.service.SagaEventLogService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeleteManagerSagaHandler {

    private final RabbitTemplate rabbitTemplate;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaEventLogService sagaEventLogService;
    private final ObjectMapper objectMapper;

    public DeleteManagerSagaHandler(
            final RabbitTemplate rabbitTemplate,
            final SagaInstanceRepository sagaInstanceRepository,
            final SagaEventLogService sagaEventLogService,
            final ObjectMapper objectMapper
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.sagaEventLogService = sagaEventLogService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public String start(final DeleteManagerRequest request) {

        final String sagaId = UUID.randomUUID().toString();
        final String sagaType = SagaType.DELETE_MANAGER.name();

        sagaInstanceRepository
                .findByCorrelationKeyAndSagaType(
                        request.cpf(),
                        sagaType
                )
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Delete manager saga already exists for CPF: "
                                    + request.cpf()
                    );
                });

        final DeleteManagerSagaPayload payload =
                new DeleteManagerSagaPayload(
                        request.cpf()
                );

        final SagaInstanceModel sagaInstance =
                new SagaInstanceModel();

        sagaInstance.setSagaId(sagaId);
        sagaInstance.setSagaType(sagaType);
        sagaInstance.setCorrelationKey(request.cpf());

        sagaInstance.setCurrentStep(
                DeleteManagerSagaStep.FIND_REPLACEMENT_MANAGER.name()
        );

        sagaInstance.setCurrentStatus(
                DeleteManagerSagaStatus.STARTED.name()
        );

        sagaInstance.setPayload(
                toJson(payload)
        );

        sagaInstance.setCreatedAt(
                LocalDateTime.now()
        );

        sagaInstance.setUpdatedAt(
                LocalDateTime.now()
        );

        sagaInstanceRepository.save(sagaInstance);

        sendFindReplacementManagerCommand(sagaId, payload);

        return sagaId;
    }

    private String toJson(final Object payload) {

        try {

            return objectMapper.writeValueAsString(payload);

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Could not serialize saga payload",
                    ex
            );

        }
    }

    private void sendFindReplacementManagerCommand(
            final String sagaId,
            final DeleteManagerSagaPayload payload
    ) {

        final FindReplacementManagerCommand command =
                new FindReplacementManagerCommand(
                        sagaId,
                        SagaType.DELETE_MANAGER.name(),
                        payload.getCpf(),
                        LocalDateTime.now(),
                        payload.getCpf()
                );

        publishCommand(
                sagaId,
                DeleteManagerSagaStep.FIND_REPLACEMENT_MANAGER,
                DeleteManagerSagaStatus.REPLACEMENT_MANAGER_REQUESTED,
                DeleteManagerRabbitConstants.FIND_REPLACEMENT_MANAGER_COMMAND_ROUTING_KEY,
                command
        );
    }

    private void publishCommand(
            final String sagaId,
            final DeleteManagerSagaStep step,
            final DeleteManagerSagaStatus status,
            final String routingKey,
            final Object command
    ) {
        final SagaInstanceModel saga = findSaga(sagaId);

        saga.setCurrentStep(step.name());
        saga.setCurrentStatus(status.name());
        saga.setUpdatedAt(LocalDateTime.now());
        sagaInstanceRepository.save(saga);

        rabbitTemplate.convertAndSend(
                DeleteManagerRabbitConstants.SAGA_COMMAND_EXCHANGE,
                routingKey,
                command
        );

        registerEventLog(
                saga,
                step,
                status,
                command.getClass().getSimpleName(),
                routingKey,
                command,
                null
        );
    }

    private void updateSaga(
            final SagaInstanceModel saga,
            final DeleteManagerSagaStep step,
            final DeleteManagerSagaStatus status,
            final DeleteManagerSagaPayload payload,
            final String errorMessage
    ) {

        saga.setCurrentStep(step.name());
        saga.setCurrentStatus(status.name());
        saga.setPayload(toJson(payload));
        saga.setErrorMessage(errorMessage);
        saga.setUpdatedAt(LocalDateTime.now());

        sagaInstanceRepository.save(saga);
    }

    private SagaInstanceModel findSaga(final String sagaId) {

        return sagaInstanceRepository
                .findBySagaId(sagaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Saga not found: " + sagaId
                        )
                );
    }

    private DeleteManagerSagaPayload readPayload(
            final SagaInstanceModel saga
    ) {

        try {

            return objectMapper.readValue(
                    saga.getPayload(),
                    DeleteManagerSagaPayload.class
            );

        } catch (JsonProcessingException ex) {

            throw new IllegalStateException(
                    "Could not read saga payload for sagaId: "
                            + saga.getSagaId(),
                    ex
            );

        }
    }

    private void registerEventLog(
            final SagaInstanceModel saga,
            final DeleteManagerSagaStep step,
            final DeleteManagerSagaStatus status,
            final String messageType,
            final String routingKey,
            final Object payload,
            final String errorMessage
    ) {

        sagaEventLogService.register(
                saga.getSagaId(),
                saga.getSagaType(),
                step.name(),
                status.name(),
                messageType,
                routingKey,
                payload,
                errorMessage
        );

    }

    @Transactional
    public void handleReplacementManagerFound(
            final ReplacementManagerFoundEvent event
    ) {

        final SagaInstanceModel saga =
                findSaga(event.sagaId());

        final DeleteManagerSagaPayload payload =
                readPayload(saga);

        payload.setManagerId(
                event.managerId()
        );

        payload.setReplacementManagerId(
                event.replacementManagerId()
        );

        updateSaga(
                saga,
                DeleteManagerSagaStep.TRANSFER_CUSTOMERS,
                DeleteManagerSagaStatus.REPLACEMENT_MANAGER_FOUND,
                payload,
                null
        );

        sendTransferCustomersCommand(
                saga.getSagaId(),
                payload
        );

    }

    private void sendTransferCustomersCommand(
            final String sagaId,
            final DeleteManagerSagaPayload payload
    ) {

        final TransferCustomersCommand command =
                new TransferCustomersCommand(
                        sagaId,
                        SagaType.DELETE_MANAGER.name(),
                        payload.getCpf(),
                        LocalDateTime.now(),
                        payload.getManagerId(),
                        payload.getReplacementManagerId()
                );

        publishCommand(
                sagaId,
                DeleteManagerSagaStep.TRANSFER_CUSTOMERS,
                DeleteManagerSagaStatus.CUSTOMER_TRANSFER_REQUESTED,
                DeleteManagerRabbitConstants.TRANSFER_CUSTOMERS_COMMAND_ROUTING_KEY,
                command
        );

    }

    @Transactional
    public void handleCustomersTransferred(
            final CustomersTransferredEvent event
    ) {

        final SagaInstanceModel saga =
                findSaga(event.sagaId());

        final DeleteManagerSagaPayload payload =
                readPayload(saga);

        payload.setTransferredCustomers(
                event.transferredCustomers()
        );

        updateSaga(
                saga,
                DeleteManagerSagaStep.DELETE_MANAGER,
                DeleteManagerSagaStatus.CUSTOMERS_TRANSFERRED,
                payload,
                null
        );

        sendDeleteManagerCommand(
                saga.getSagaId(),
                payload
        );

    }
    private void sendDeleteManagerCommand(
            final String sagaId,
            final DeleteManagerSagaPayload payload
    ) {

        final DeleteManagerCommand command =
                new DeleteManagerCommand(
                        sagaId,
                        SagaType.DELETE_MANAGER.name(),
                        payload.getCpf(),
                        LocalDateTime.now(),
                        payload.getManagerId(),
                        payload.getReplacementManagerId()
                );

        publishCommand(
                sagaId,
                DeleteManagerSagaStep.DELETE_MANAGER,
                DeleteManagerSagaStatus.MANAGER_DELETION_REQUESTED,
                DeleteManagerRabbitConstants.DELETE_MANAGER_COMMAND_ROUTING_KEY,
                command
        );

    }

    @Transactional
    public void handleManagerDeleted(
            final ManagerDeletedEvent event
    ) {

        final SagaInstanceModel saga =
                findSaga(event.sagaId());

        final DeleteManagerSagaPayload payload =
                readPayload(saga);

        updateSaga(
                saga,
                DeleteManagerSagaStep.COMPLETE,
                DeleteManagerSagaStatus.COMPLETED,
                payload,
                null
        );

        registerEventLog(
                saga,
                DeleteManagerSagaStep.COMPLETE,
                DeleteManagerSagaStatus.COMPLETED,
                "DeleteManagerCompleted",
                null,
                payload,
                null
        );

    }

    @Transactional
    public void handleReplacementManagerNotFound(
            final ReplacementManagerNotFoundEvent event
    ) {

        final SagaInstanceModel saga =
                findSaga(event.sagaId());

        updateSaga(
                saga,
                DeleteManagerSagaStep.FIND_REPLACEMENT_MANAGER,
                DeleteManagerSagaStatus.FAILED,
                readPayload(saga),
                event.errorMessage()
        );

        registerEventLog(
                saga,
                DeleteManagerSagaStep.FIND_REPLACEMENT_MANAGER,
                DeleteManagerSagaStatus.FAILED,
                "ReplacementManagerNotFoundEvent",
                DeleteManagerRabbitConstants.REPLACEMENT_MANAGER_NOT_FOUND_EVENT_ROUTING_KEY,
                event,
                event.errorMessage()
        );

    }

    @Transactional
    public void handleCustomerTransferFailed(
            final CustomerTransferFailedEvent event
    ) {

        final SagaInstanceModel saga =
                findSaga(event.sagaId());

        updateSaga(
                saga,
                DeleteManagerSagaStep.TRANSFER_CUSTOMERS,
                DeleteManagerSagaStatus.FAILED,
                readPayload(saga),
                event.errorMessage()
        );

        registerEventLog(
                saga,
                DeleteManagerSagaStep.TRANSFER_CUSTOMERS,
                DeleteManagerSagaStatus.FAILED,
                "CustomerTransferFailedEvent",
                DeleteManagerRabbitConstants.CUSTOMER_TRANSFER_FAILED_EVENT_ROUTING_KEY,
                event,
                event.errorMessage()
        );

    }

    @Transactional
    public void handleManagerDeletionFailed(
            final ManagerDeletionFailedEvent event
    ) {

        final SagaInstanceModel saga =
                findSaga(event.sagaId());

        updateSaga(
                saga,
                DeleteManagerSagaStep.DELETE_MANAGER,
                DeleteManagerSagaStatus.FAILED,
                readPayload(saga),
                event.errorMessage()
        );

        registerEventLog(
                saga,
                DeleteManagerSagaStep.DELETE_MANAGER,
                DeleteManagerSagaStatus.FAILED,
                "ManagerDeletionFailedEvent",
                DeleteManagerRabbitConstants.MANAGER_DELETION_FAILED_EVENT_ROUTING_KEY,
                event,
                event.errorMessage()
        );

    }
}
