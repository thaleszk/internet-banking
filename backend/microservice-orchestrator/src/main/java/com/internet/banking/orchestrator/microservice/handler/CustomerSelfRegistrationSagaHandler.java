package com.internet.banking.orchestrator.microservice.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internet.banking.orchestrator.microservice.command.*;
import com.internet.banking.orchestrator.microservice.config.CustomerSelfRegistrationRabbitConstants;
import com.internet.banking.orchestrator.microservice.dto.CustomerSelfRegistrationRequest;
import com.internet.banking.orchestrator.microservice.dto.CustomerSelfRegistrationSagaPayload;
import com.internet.banking.orchestrator.microservice.enums.CustomerSelfRegistrationSagaStatus;
import com.internet.banking.orchestrator.microservice.enums.CustomerSelfRegistrationSagaStep;
import com.internet.banking.orchestrator.microservice.enums.SagaType;
import com.internet.banking.orchestrator.microservice.event.*;
import com.internet.banking.orchestrator.microservice.model.SagaInstanceModel;
import com.internet.banking.orchestrator.microservice.repository.SagaInstanceRepository;
import com.internet.banking.orchestrator.microservice.service.SagaEventLogService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomerSelfRegistrationSagaHandler {

    private final RabbitTemplate rabbitTemplate;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaEventLogService sagaEventLogService;
    private final ObjectMapper objectMapper;
    private final String customerServiceUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public CustomerSelfRegistrationSagaHandler(
            final RabbitTemplate rabbitTemplate,
            final SagaInstanceRepository sagaInstanceRepository,
            final SagaEventLogService sagaEventLogService,
            final ObjectMapper objectMapper,
            @Value("${customer.service.url:http://localhost:8082}") final String customerServiceUrl
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.sagaEventLogService = sagaEventLogService;
        this.objectMapper = objectMapper;
        this.customerServiceUrl = customerServiceUrl;
    }

    @Transactional
    public String start(final CustomerSelfRegistrationRequest request) {
        final String sagaId = UUID.randomUUID().toString();
        final String sagaType = SagaType.CUSTOMER_SELF_REGISTRATION.name();

        sagaInstanceRepository.findByCorrelationKeyAndSagaType(request.cpf(), sagaType)
                .ifPresent(existing -> {
                    throw new IllegalStateException("Customer self registration already exists for CPF: " + request.cpf());
                });

        final CustomerSelfRegistrationSagaPayload payload =
                new CustomerSelfRegistrationSagaPayload(
                        request.name(),
                        request.cpf(),
                        request.email(),
                        request.phone()
                );

        final SagaInstanceModel sagaInstance = new SagaInstanceModel();
        sagaInstance.setSagaId(sagaId);
        sagaInstance.setSagaType(sagaType);
        sagaInstance.setCorrelationKey(request.cpf());
        sagaInstance.setCurrentStep(CustomerSelfRegistrationSagaStep.CREATE_CUSTOMER.name());
        sagaInstance.setCurrentStatus(CustomerSelfRegistrationSagaStatus.STARTED.name());
        sagaInstance.setPayload(toJson(payload));
        sagaInstance.setCreatedAt(LocalDateTime.now());
        sagaInstance.setUpdatedAt(LocalDateTime.now());

        sagaInstanceRepository.save(sagaInstance);

        createPendingCustomer(request);
        sendCreateCustomerCommand(sagaId, payload);

        return sagaId;
    }

    private void createPendingCustomer(CustomerSelfRegistrationRequest request) {
        Map<String, Object> address = Map.of(
                "streetName", request.address() == null || request.address().isBlank() ? "Endereco nao informado" : request.address(),
                "streetNumber", "S/N",
                "zipCode", request.cep() == null || request.cep().isBlank() ? "00000000" : request.cep(),
                "city", request.city() == null || request.city().isBlank() ? "Curitiba" : request.city(),
                "state", request.state() == null || request.state().isBlank() ? "PR" : request.state()
        );

        Map<String, Object> body = Map.of(
                "name", request.name(),
                "email", request.email(),
                "cpf", request.cpf(),
                "phone", request.phone() == null ? "" : request.phone(),
                "salary", request.salary(),
                "address", address
        );

        try {
            restTemplate.postForEntity(customerServiceUrl + "/customers/registration/request", body, Map.class);
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(409))) {
                throw new IllegalStateException("Customer self registration already exists for CPF: " + request.cpf());
            }
            throw exception;
        }
    }

    @Transactional
    public void handleCustomerCreated(final CustomerCreatedForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());
        final CustomerSelfRegistrationSagaPayload payload = readPayload(saga);

        payload.setCustomerId(event.customerId());

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.ASSIGN_ACCOUNT_MANAGER,
                CustomerSelfRegistrationSagaStatus.CUSTOMER_CREATED,
                payload,
                null
        );

        sendAssignManagerCommand(saga.getSagaId(), payload);
    }

    @Transactional
    public void handleCustomerCreationFailed(final CustomerCreationFailedForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.CREATE_CUSTOMER,
                CustomerSelfRegistrationSagaStatus.FAILED,
                readPayload(saga),
                event.errorMessage()
        );

        registerEventLog(
                saga,
                CustomerSelfRegistrationSagaStep.CREATE_CUSTOMER,
                CustomerSelfRegistrationSagaStatus.FAILED,
                "CustomerCreationFailedForSelfRegistrationEvent",
                CustomerSelfRegistrationRabbitConstants.CUSTOMER_CREATION_FAILED_EVENT_ROUTING_KEY,
                event,
                event.errorMessage()
        );
    }

    @Transactional
    public void handleManagerAssigned(final ManagerAssignedForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());
        final CustomerSelfRegistrationSagaPayload payload = readPayload(saga);

        payload.setManagerId(event.managerId());

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.CREATE_ACCOUNT,
                CustomerSelfRegistrationSagaStatus.MANAGER_ASSIGNED,
                payload,
                null
        );

        sendCreateAccountCommand(saga.getSagaId(), payload);
    }

    @Transactional
    public void handleManagerAssignmentFailed(final ManagerAssignmentFailedForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.ASSIGN_ACCOUNT_MANAGER,
                CustomerSelfRegistrationSagaStatus.COMPENSATION_STARTED,
                readPayload(saga),
                event.errorMessage()
        );

        sendDeactivateCustomerCommand(saga, "Manager assignment failed: " + event.errorMessage());
    }

    @Transactional
    public void handleAccountCreated(final AccountCreatedForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());
        final CustomerSelfRegistrationSagaPayload payload = readPayload(saga);

        payload.setAccountId(event.accountId());

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.CREATE_AUTHENTICATION,
                CustomerSelfRegistrationSagaStatus.ACCOUNT_CREATED,
                payload,
                null
        );

        sendCreateAuthenticationCommand(saga.getSagaId(), payload);
    }

    @Transactional
    public void handleAccountCreationFailed(final AccountCreationFailedForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.CREATE_ACCOUNT,
                CustomerSelfRegistrationSagaStatus.COMPENSATION_STARTED,
                readPayload(saga),
                event.errorMessage()
        );

        sendDeactivateCustomerCommand(saga, "Account creation failed: " + event.errorMessage());
    }

    @Transactional
    public void handleAuthenticationCreated(final AuthenticationCreatedForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());
        final CustomerSelfRegistrationSagaPayload payload = readPayload(saga);

        payload.setAuthenticationId(event.authenticationId());

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.COMPLETE_REGISTRATION,
                CustomerSelfRegistrationSagaStatus.COMPLETED,
                payload,
                null
        );

        registerEventLog(
                saga,
                CustomerSelfRegistrationSagaStep.COMPLETE_REGISTRATION,
                CustomerSelfRegistrationSagaStatus.COMPLETED,
                "CustomerSelfRegistrationCompleted",
                null,
                payload,
                null
        );
    }

    @Transactional
    public void handleAuthenticationCreationFailed(final AuthenticationCreationFailedForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());
        final CustomerSelfRegistrationSagaPayload payload = readPayload(saga);

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.CREATE_AUTHENTICATION,
                CustomerSelfRegistrationSagaStatus.COMPENSATION_STARTED,
                payload,
                event.errorMessage()
        );

        if (payload.getAccountId() != null) {
            sendCancelAccountCommand(saga, "Authentication creation failed: " + event.errorMessage());
            return;
        }

        sendDeactivateCustomerCommand(saga, "Authentication creation failed: " + event.errorMessage());
    }

    @Transactional
    public void handleAccountCancelled(final AccountCancelledForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.CANCEL_ACCOUNT,
                CustomerSelfRegistrationSagaStatus.ACCOUNT_CANCELLED,
                readPayload(saga),
                saga.getErrorMessage()
        );

        sendDeactivateCustomerCommand(saga, "Account cancelled after authentication failure");
    }

    @Transactional
    public void handleCustomerDeactivated(final CustomerDeactivatedForSelfRegistrationEvent event) {
        final SagaInstanceModel saga = findSaga(event.sagaId());

        updateSaga(
                saga,
                CustomerSelfRegistrationSagaStep.DEACTIVATE_CUSTOMER,
                CustomerSelfRegistrationSagaStatus.FAILED,
                readPayload(saga),
                saga.getErrorMessage()
        );
    }

    private void sendCreateCustomerCommand(
            final String sagaId,
            final CustomerSelfRegistrationSagaPayload payload
    ) {
        final CreateCustomerForSelfRegistrationCommand command =
                new CreateCustomerForSelfRegistrationCommand(
                        sagaId,
                        SagaType.CUSTOMER_SELF_REGISTRATION.name(),
                        payload.getCpf(),
                        LocalDateTime.now(),
                        payload.getName(),
                        payload.getCpf(),
                        payload.getEmail(),
                        payload.getPhone()
                );

        publishCommand(
                sagaId,
                CustomerSelfRegistrationSagaStep.CREATE_CUSTOMER,
                CustomerSelfRegistrationSagaStatus.CUSTOMER_CREATION_REQUESTED,
                CustomerSelfRegistrationRabbitConstants.CREATE_CUSTOMER_COMMAND_ROUTING_KEY,
                command
        );
    }

    private void sendAssignManagerCommand(
            final String sagaId,
            final CustomerSelfRegistrationSagaPayload payload
    ) {
        final AssignManagerForSelfRegistrationCommand command =
                new AssignManagerForSelfRegistrationCommand(
                        sagaId,
                        SagaType.CUSTOMER_SELF_REGISTRATION.name(),
                        payload.getCpf(),
                        LocalDateTime.now(),
                        payload.getCustomerId(),
                        payload.getCpf()
                );

        publishCommand(
                sagaId,
                CustomerSelfRegistrationSagaStep.ASSIGN_ACCOUNT_MANAGER,
                CustomerSelfRegistrationSagaStatus.MANAGER_ASSIGNMENT_REQUESTED,
                CustomerSelfRegistrationRabbitConstants.ASSIGN_MANAGER_COMMAND_ROUTING_KEY,
                command
        );
    }

    private void sendCreateAccountCommand(
            final String sagaId,
            final CustomerSelfRegistrationSagaPayload payload
    ) {
        final CreateAccountForSelfRegistrationCommand command =
                new CreateAccountForSelfRegistrationCommand(
                        sagaId,
                        SagaType.CUSTOMER_SELF_REGISTRATION.name(),
                        payload.getCpf(),
                        LocalDateTime.now(),
                        payload.getCustomerId(),
                        payload.getManagerId()
                );

        publishCommand(
                sagaId,
                CustomerSelfRegistrationSagaStep.CREATE_ACCOUNT,
                CustomerSelfRegistrationSagaStatus.ACCOUNT_CREATION_REQUESTED,
                CustomerSelfRegistrationRabbitConstants.CREATE_ACCOUNT_COMMAND_ROUTING_KEY,
                command
        );
    }

    private void sendCreateAuthenticationCommand(
            final String sagaId,
            final CustomerSelfRegistrationSagaPayload payload
    ) {
        final CreateAuthenticationForSelfRegistrationCommand command =
                new CreateAuthenticationForSelfRegistrationCommand(
                        sagaId,
                        SagaType.CUSTOMER_SELF_REGISTRATION.name(),
                        payload.getCpf(),
                        LocalDateTime.now(),
                        payload.getCustomerId(),
                        payload.getCpf(),
                        payload.getEmail()
                );

        publishCommand(
                sagaId,
                CustomerSelfRegistrationSagaStep.CREATE_AUTHENTICATION,
                CustomerSelfRegistrationSagaStatus.AUTHENTICATION_CREATION_REQUESTED,
                CustomerSelfRegistrationRabbitConstants.CREATE_AUTHENTICATION_COMMAND_ROUTING_KEY,
                command
        );
    }

    private void sendCancelAccountCommand(
            final SagaInstanceModel saga,
            final String reason
    ) {
        final CustomerSelfRegistrationSagaPayload payload = readPayload(saga);

        final CancelAccountForSelfRegistrationCommand command =
                new CancelAccountForSelfRegistrationCommand(
                        saga.getSagaId(),
                        SagaType.CUSTOMER_SELF_REGISTRATION.name(),
                        saga.getCorrelationKey(),
                        LocalDateTime.now(),
                        payload.getAccountId(),
                        reason
                );

        publishCommand(
                saga.getSagaId(),
                CustomerSelfRegistrationSagaStep.CANCEL_ACCOUNT,
                CustomerSelfRegistrationSagaStatus.ACCOUNT_CANCELLATION_REQUESTED,
                CustomerSelfRegistrationRabbitConstants.CANCEL_ACCOUNT_COMMAND_ROUTING_KEY,
                command
        );
    }

    private void sendDeactivateCustomerCommand(
            final SagaInstanceModel saga,
            final String reason
    ) {
        final CustomerSelfRegistrationSagaPayload payload = readPayload(saga);

        final DeactivateCustomerForSelfRegistrationCommand command =
                new DeactivateCustomerForSelfRegistrationCommand(
                        saga.getSagaId(),
                        SagaType.CUSTOMER_SELF_REGISTRATION.name(),
                        saga.getCorrelationKey(),
                        LocalDateTime.now(),
                        payload.getCustomerId(),
                        reason
                );

        publishCommand(
                saga.getSagaId(),
                CustomerSelfRegistrationSagaStep.DEACTIVATE_CUSTOMER,
                CustomerSelfRegistrationSagaStatus.CUSTOMER_DEACTIVATION_REQUESTED,
                CustomerSelfRegistrationRabbitConstants.DEACTIVATE_CUSTOMER_COMMAND_ROUTING_KEY,
                command
        );
    }

    private void publishCommand(
            final String sagaId,
            final CustomerSelfRegistrationSagaStep step,
            final CustomerSelfRegistrationSagaStatus status,
            final String routingKey,
            final Object command
    ) {
        final SagaInstanceModel saga = findSaga(sagaId);

        saga.setCurrentStep(step.name());
        saga.setCurrentStatus(status.name());
        saga.setUpdatedAt(LocalDateTime.now());
        sagaInstanceRepository.save(saga);

        rabbitTemplate.convertAndSend(
                CustomerSelfRegistrationRabbitConstants.SAGA_COMMAND_EXCHANGE,
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
            final CustomerSelfRegistrationSagaStep step,
            final CustomerSelfRegistrationSagaStatus status,
            final CustomerSelfRegistrationSagaPayload payload,
            final String errorMessage
    ) {
        saga.setCurrentStep(step.name());
        saga.setCurrentStatus(status.name());
        saga.setPayload(toJson(payload));
        saga.setErrorMessage(errorMessage);
        saga.setUpdatedAt(LocalDateTime.now());

        sagaInstanceRepository.save(saga);
    }

    private void registerEventLog(
            final SagaInstanceModel saga,
            final CustomerSelfRegistrationSagaStep step,
            final CustomerSelfRegistrationSagaStatus status,
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

    private SagaInstanceModel findSaga(final String sagaId) {
        return sagaInstanceRepository.findBySagaId(sagaId)
                .orElseThrow(() -> new IllegalArgumentException("Saga not found: " + sagaId));
    }

    private CustomerSelfRegistrationSagaPayload readPayload(final SagaInstanceModel saga) {
        try {
            return objectMapper.readValue(
                    saga.getPayload(),
                    CustomerSelfRegistrationSagaPayload.class
            );
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not read saga payload for sagaId: " + saga.getSagaId(), ex);
        }
    }

    private String toJson(final Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize saga payload", ex);
        }
    }
}
