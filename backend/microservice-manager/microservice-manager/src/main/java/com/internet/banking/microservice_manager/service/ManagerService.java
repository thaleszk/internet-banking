package com.internet.banking.manager.microservice.service;

import com.internet.banking.manager.microservice.data.ManagerData;
import com.internet.banking.manager.microservice.model.ManagerModel;

import java.util.List;

public interface ManagerService {

    ManagerModel createManager(ManagerData managerData);

    ManagerModel getManagerByCpf(String cpf);

    List<ManagerModel> getAllManagers();

    ManagerModel updateManager(String cpf, ManagerModel managerModel);

    void deleteManager(String cpf);
}