package com.internet.banking.microservice_manager.service;

import com.internet.banking.microservice_manager.dto.DeleteManagerEvent;

public interface ManagerSagaService {

    void validateDeletion(DeleteManagerEvent event);
}
