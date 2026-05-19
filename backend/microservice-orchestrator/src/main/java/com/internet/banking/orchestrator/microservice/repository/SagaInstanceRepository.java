package com.internet.banking.orchestrator.microservice.repository;

import com.internet.banking.orchestrator.microservice.model.SagaInstanceModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SagaInstanceRepository extends JpaRepository<SagaInstanceModel, Long> {

    Optional<SagaInstanceModel> findBySagaId(String sagaId);

    Optional<SagaInstanceModel> findByCorrelationKeyAndSagaType(
            String correlationKey,
            String sagaType
    );
}
