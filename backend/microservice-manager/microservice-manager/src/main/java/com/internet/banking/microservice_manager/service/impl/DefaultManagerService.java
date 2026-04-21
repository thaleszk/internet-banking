package com.internet.banking.manager.microservice.service.impl;
import com.internet.banking.manager.microservice.dao.ManagerDao;
import com.internet.banking.manager.microservice.data.ManagerData;
import com.internet.banking.manager.microservice.exception.ManagerAlreadyExistsException;
import com.internet.banking.manager.microservice.exception.ManagerNotFoundException;
import com.internet.banking.manager.microservice.mapper.ManagerMapper;
import com.internet.banking.manager.microservice.model.ManagerModel;
import com.internet.banking.manager.microservice.service.ManagerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultManagerService implements ManagerService {

    private final ManagerDao managerDao;

    public DefaultManagerService(final ManagerDao managerDao) {
        this.managerDao = managerDao;
    }

    @Override
    public ManagerModel createManager(final ManagerModel managerModel) {
        validateManagerModel(managerModel);

        if (managerDao.existsByCpf(managerModel.getCpf())) {
            throw new ManagerAlreadyExistsException(
                    "Manager already exists for CPF: " + managerModel.getCpf()
            );
        }

        ManagerData managerData = ManagerMapper.toData(managerModel);
        ManagerData savedManager = managerDao.save(managerData);

        return ManagerMapper.toModel(savedManager);
    }

    @Override
    public ManagerModel getManagerByCpf(final String cpf) {
        ManagerData managerData = managerDao.findByCpf(cpf)
                .orElseThrow(() -> new ManagerNotFoundException("Manager not found for CPF: " + cpf));

        return ManagerMapper.toModel(managerData);
    }

    @Override
    public List<ManagerModel> getAllManagers() {
        return managerDao.findAll()
                .stream()
                .map(ManagerMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public ManagerModel updateManager(final String cpf, final ManagerModel managerModel) {
        validateManagerModel(managerModel);

        if (!managerDao.existsByCpf(cpf)) {
            throw new ManagerNotFoundException("Manager not found for CPF: " + cpf);
        }

        managerModel.setCpf(cpf);

        ManagerData managerData = ManagerMapper.toData(managerModel);
        ManagerData updatedManager = managerDao.update(cpf, managerData);

        return ManagerMapper.toModel(updatedManager);
    }

    @Override
    public void deleteManager(final String cpf) {
        if (!managerDao.existsByCpf(cpf)) {
            throw new ManagerNotFoundException("Manager not found for CPF: " + cpf);
        }

        managerDao.delete(cpf);
    }

    private void validateManagerModel(final ManagerModel managerModel) {
        if (managerModel == null) {
            throw new IllegalArgumentException("Manager must not be null");
        }

        if (managerModel.getCpf() == null || managerModel.getCpf().isBlank()) {
            throw new IllegalArgumentException("Manager CPF must not be null or blank");
        }

        if (managerModel.getAddress() == null) {
            throw new IllegalArgumentException("Manager address must not be null");
        }
    }
}
