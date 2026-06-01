package com.internet.banking.microservice_manager.service.impl;

import com.internet.banking.microservice_manager.dto.DeleteManagerEvent;
import com.internet.banking.microservice_manager.producer.ManagerSagaProducer;
import com.internet.banking.microservice_manager.dao.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerSagaService implements com.internet.banking.microservice_manager.service.ManagerSagaService {

    private final ManagerRepository repository;

    private final ManagerSagaProducer producer;

    public void validateDeletion(
            DeleteManagerEvent event
    ) {

        long totalManagers = repository.count();

        if (totalManagers <= 1) {

            producer.sendValidationFailed(
                    event.getCpf()
            );

            throw new RuntimeException(
                    "Nao pode remover o ultimo gerente"
            );
        }

        producer.sendTransferRequest(
                event
        );
    }
}
