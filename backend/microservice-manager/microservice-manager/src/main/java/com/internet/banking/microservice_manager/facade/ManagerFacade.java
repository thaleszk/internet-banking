package com.internet.banking.microservice_manager.facade;

import com.internet.banking.microservice_manager.data.ManagerData;
import com.internet.banking.microservice_manager.model.ManagerModel;

import java.util.List;

public interface ManagerFacade {

    ManagerData createManager(ManagerData managerData);

    ManagerModel getManagerByCpf(String cpf);

    List<ManagerModel> getAllManagers();

    ManagerModel updateManager(String cpf, ManagerModel managerModel);

    void deleteManager(String cpf);
}