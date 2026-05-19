package com.internet.banking.microservice_manager.facade.impl;

import com.internet.banking.manager.microservice.data.ManagerData;
import com.internet.banking.manager.microservice.facade.ManagerFacade;
import com.internet.banking.manager.microservice.mapper.ManagerMapper;
import com.internet.banking.manager.microservice.model.ManagerModel;
import com.internet.banking.manager.microservice.service.ManagerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultManagerFacade implements ManagerFacade {

    private final ManagerService managerService;

    public DefaultManagerFacade(final ManagerService managerService) {
        this.managerService = managerService;
    }

    @Override
    public ManagerData createManager(final ManagerData managerData) {
        ManagerModel created = managerService.createManager(managerData);
        return ManagerMapper.toData(created);
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

    @Override
    public void requestDeleteManager(String cpf) {
        rabbitTemplate.convertAndSend(
                "orchestrator.exchange",
                "manager.delete.requested",
                cpf
        );
    }
}