package com.internet.banking.orchestrator.microservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.internet.banking.orchestrator.microservice.model.SagaEventLogModel;
import com.internet.banking.orchestrator.microservice.repository.SagaEventLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SagaEventLogService {

    private final SagaEventLogRepository sagaEventLogRepository;
    private final ObjectMapper objectMapper;

    public SagaEventLogService(
            final SagaEventLogRepository sagaEventLogRepository,
            final ObjectMapper objectMapper
    ) {
        this.sagaEventLogRepository = sagaEventLogRepository;
        this.objectMapper = objectMapper;
    }

    public void register(
            final String sagaId,
            final String sagaType,
            final String step,
            final String status,
            final String messageType,
            final String routingKey,
            final Object payload,
            final String errorMessage
    ) {
        final SagaEventLogModel eventLog = new SagaEventLogModel();

        eventLog.setSagaId(sagaId);
        eventLog.setSagaType(sagaType);
        eventLog.setStep(step);
        eventLog.setStatus(status);
        eventLog.setMessageType(messageType);
        eventLog.setRoutingKey(routingKey);
        eventLog.setPayload(toJson(payload));
        eventLog.setErrorMessage(errorMessage);
        eventLog.setCreatedAt(LocalDateTime.now());

        sagaEventLogRepository.save(eventLog);
    }

    private String toJson(final Object payload) {
        if (payload == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            return "{\"serializationError\":\"Could not serialize payload\"}";
        }
    }
}
