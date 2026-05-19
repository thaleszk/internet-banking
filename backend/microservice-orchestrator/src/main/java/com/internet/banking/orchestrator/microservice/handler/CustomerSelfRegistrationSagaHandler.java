package com.internet.banking.orchestrator.microservice.handler;

@Service
public class CustomerSelfRegistrationSagaHandler {

    private final RabbitTemplate rabbitTemplate;
    private final SagaInstanceRepository sagaInstanceRepository;

    public CustomerSelfRegistrationSagaHandler(
            final RabbitTemplate rabbitTemplate,
            final SagaInstanceRepository sagaInstanceRepository
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.sagaInstanceRepository = sagaInstanceRepository;
    }

    public String start(final CustomerSelfRegistrationRequest request) {
        final String sagaId = UUID.randomUUID().toString();

        final SagaInstance sagaInstance = new SagaInstance();
        sagaInstance.setSagaId(sagaId);
        sagaInstance.setSagaType(SagaType.CUSTOMER_SELF_REGISTRATION.name());
        sagaInstance.setCorrelationKey(request.getCpf());
        sagaInstance.setCurrentStep(CustomerSelfRegistrationSagaStep.CREATE_CUSTOMER.name());
        sagaInstance.setCurrentStatus(CustomerSelfRegistrationSagaStatus.STARTED.name());
        sagaInstance.setCreatedAt(LocalDateTime.now());

        sagaInstanceRepository.save(sagaInstance);

        final CreateCustomerForSelfRegistrationCommand command =
                new CreateCustomerForSelfRegistrationCommand();

        command.setSagaId(sagaId);
        command.setSagaType(SagaType.CUSTOMER_SELF_REGISTRATION.name());
        command.setCorrelationId(request.getCpf());
        command.setOccurredAt(LocalDateTime.now());
        command.setName(request.getName());
        command.setCpf(request.getCpf());
        command.setEmail(request.getEmail());
        command.setPhone(request.getPhone());

        updateSagaStatus(
                sagaId,
                CustomerSelfRegistrationSagaStep.CREATE_CUSTOMER,
                CustomerSelfRegistrationSagaStatus.CUSTOMER_CREATION_REQUESTED
        );

        rabbitTemplate.convertAndSend(
                CustomerSelfRegistrationRabbitConstants.SAGA_COMMAND_EXCHANGE,
                CustomerSelfRegistrationRabbitConstants.CREATE_CUSTOMER_COMMAND_ROUTING_KEY,
                command
        );

        return sagaId;
    }
}
