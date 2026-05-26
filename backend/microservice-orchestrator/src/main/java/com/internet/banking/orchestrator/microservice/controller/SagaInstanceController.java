package com.internet.banking.orchestrator.microservice.controller;

import com.internet.banking.orchestrator.microservice.model.SagaEventLogModel;
import com.internet.banking.orchestrator.microservice.model.SagaInstanceModel;
import com.internet.banking.orchestrator.microservice.repository.SagaEventLogRepository;
import com.internet.banking.orchestrator.microservice.repository.SagaInstanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sagas")
public class SagaInstanceController {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaEventLogRepository sagaEventLogRepository;

    public SagaInstanceController(
            final SagaInstanceRepository sagaInstanceRepository,
            final SagaEventLogRepository sagaEventLogRepository
    ) {
        this.sagaInstanceRepository = sagaInstanceRepository;
        this.sagaEventLogRepository = sagaEventLogRepository;
    }

    @GetMapping("/{sagaId}")
    public ResponseEntity<SagaInstanceModel> findBySagaId(
            @PathVariable final String sagaId
    ) {
        return sagaInstanceRepository.findBySagaId(sagaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{sagaId}/events")
    public ResponseEntity<List<SagaEventLogModel>> findEventsBySagaId(
            @PathVariable final String sagaId
    ) {
        return ResponseEntity.ok(
                sagaEventLogRepository.findBySagaIdOrderByCreatedAtAsc(sagaId)
        );
    }
}
