package com.internet.banking.microservice_manager.service.impl;

import com.internet.banking.microservice_manager.dao.ManagerRepository;
import com.internet.banking.microservice_manager.data.ManagerData;
import com.internet.banking.microservice_manager.exception.ManagerAlreadyExistsException;
import com.internet.banking.microservice_manager.exception.ManagerNotFoundException;
import com.internet.banking.microservice_manager.mapper.ManagerMapper;
import com.internet.banking.microservice_manager.model.ManagerModel;
import com.internet.banking.microservice_manager.service.ManagerService;
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
            throw new IllegalArgumentException("CPF do gerente e obrigatorio");
        }

        if (managerRepository.existsByCpf(managerData.getCpf())) {
            throw new ManagerAlreadyExistsException(
                    "Gerente ja cadastrado para o CPF: " + managerData.getCpf()
            );
        }

        ManagerModel model = ManagerMapper.toModel(managerData);
        return managerRepository.save(model);
    }

    @Override
    public ManagerModel getManagerByCpf(final String cpf) {
        return managerRepository.findByCpf(cpf)
                .orElseThrow(() -> new ManagerNotFoundException("Gerente nao encontrado para o CPF: " + cpf));
    }

    @Override
    public List<ManagerModel> getAllManagers() {
        return managerRepository.findAll();
    }

    @Override
    @Transactional
    public ManagerModel updateManager(final String cpf, final ManagerModel managerModel) {
        ManagerModel existing = managerRepository.findByCpf(cpf)
                .orElseThrow(() -> new ManagerNotFoundException("Gerente nao encontrado para o CPF: " + cpf));

        if (managerModel.getName() != null) existing.setName(managerModel.getName());
        if (managerModel.getEmail() != null) existing.setEmail(managerModel.getEmail());
        if (managerModel.getPhone() != null) existing.setPhone(managerModel.getPhone());

        return managerRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteManager(final String cpf) {
        if (!managerRepository.existsByCpf(cpf)) {
            throw new ManagerNotFoundException("Gerente nao encontrado para o CPF: " + cpf);
        }
        managerRepository.deleteByCpf(cpf);
    }

    @Override
    public ManagerModel findReplacementManager(
            final String managerCpf
    ) {

        return managerRepository
                .findFirstByCpfNot(managerCpf)
                .orElseThrow(() ->
                        new ManagerNotFoundException(
                                "Nenhum gerente substituto encontrado."
                        ));

    }
}
