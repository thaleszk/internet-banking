package com.internet.banking.orchestrator.microservice.service;

import com.internet.banking.orchestrator.microservice.dto.SelfRegisterRequestDTO;
import com.internet.banking.orchestrator.microservice.producer.SagaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelfRegisterSagaService {

    private final SagaEventProducer producer;

    public void startSaga(SelfRegisterRequestDTO dto) {

        producer.send(
                "customer.create.queue",
                dto
        );
    }
}