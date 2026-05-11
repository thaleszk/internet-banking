package com.internet.banking.manager.microservice.service.impl;

import com.internet.banking.manager.microservice.dao.ManagerRepository;
import com.internet.banking.manager.microservice.data.ManagerData;
import com.internet.banking.manager.microservice.exception.ManagerAlreadyExistsException;
import com.internet.banking.manager.microservice.exception.ManagerNotFoundException;
import com.internet.banking.manager.microservice.mapper.ManagerMapper;
import com.internet.banking.manager.microservice.model.ManagerModel;
import com.internet.banking.manager.microservice.service.ManagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultManagerService implements ManagerService {

    private final ManagerRepository managerRepository;

    public DefaultManagerService(final ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    @Transactional
    public ManagerModel createManager(final ManagerData managerData) {
        if (managerData == null || managerData.getCpf() == null || managerData.getCpf().isBlank()) {
            throw new IllegalArgumentException("Manager CPF must not be null or blank");
        }

        if (managerRepository.existsByCpf(managerData.getCpf())) {
            throw new ManagerAlreadyExistsException(
                    "Manager already exists for CPF: " + managerData.getCpf()
            );
        }

        ManagerModel model = ManagerMapper.toModel(managerData);
        return managerRepository.save(model);
    }

    @Override
    public ManagerModel getManagerByCpf(final String cpf) {
        return managerRepository.findByCpf(cpf)
                .orElseThrow(() -> new ManagerNotFoundException("Manager not found for CPF: " + cpf));
    }

    @Override
    public List<ManagerModel> getAllManagers() {
        return managerRepository.findAll();
    }

    @Override
    @Transactional
    public ManagerModel updateManager(final String cpf, final ManagerModel managerModel) {
        ManagerModel existing = managerRepository.findByCpf(cpf)
                .orElseThrow(() -> new ManagerNotFoundException("Manager not found for CPF: " + cpf));

        if (managerModel.getName() != null) existing.setName(managerModel.getName());
        if (managerModel.getEmail() != null) existing.setEmail(managerModel.getEmail());
        if (managerModel.getPhone() != null) existing.setPhone(managerModel.getPhone());

        return managerRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteManager(final String cpf) {
        if (!managerRepository.existsByCpf(cpf)) {
            throw new ManagerNotFoundException("Manager not found for CPF: " + cpf);
        }
        managerRepository.deleteByCpf(cpf);
    }
}