package com.internet.banking.orchestrator.microservice.service;

import com.internet.banking.orchestrator.microservice.dto.DeleteManagerEvent;
import com.internet.banking.orchestrator.microservice.producer.SagaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerSagaService {

    private final SagaEventProducer producer;

    public void start(DeleteManagerEvent event) {
        producer.sendDeleteValidation(event);
    }
}