package com.internet.banking.manager.microservice.facade.impl;

import com.internet.banking.manager.microservice.data.ManagerData;
import com.internet.banking.manager.microservice.facade.ManagerFacade;
import com.internet.banking.manager.microservice.model.ManagerModel;
import com.internet.banking.manager.microservice.service.ManagerService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultManagerFacade implements ManagerFacade {

    private final ManagerService managerService;

    public DefaultManagerFacade(final ManagerService managerService) {
        this.managerService = managerService;
    }

    @Override
    public ManagerModel createManager(final ManagerData managerData) {
        return managerService.createManager(managerData);
    }

    @Override
    public ManagerModel getManagerByCpf(final String cpf) {
        return managerService.getManagerByCpf(cpf);
    }

    @Override
    public List<ManagerModel> getAllManagers() {
        return managerService.getAllManagers();
    }

    @Override
    public ManagerModel updateManager(final String cpf, final ManagerModel managerModel) {
        return managerService.updateManager(cpf, managerModel);
    }

    @Override
    public void deleteManager(final String cpf) {
        managerService.deleteManager(cpf);
    }
}
