package com.internet.banking.orchestrator.microservice.repository;

import com.internet.banking.orchestrator.microservice.model.SagaEventLogModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SagaEventLogRepository extends JpaRepository<SagaEventLogModel, Long> {

    List<SagaEventLogModel> findBySagaIdOrderByCreatedAtAsc(String sagaId);
}
