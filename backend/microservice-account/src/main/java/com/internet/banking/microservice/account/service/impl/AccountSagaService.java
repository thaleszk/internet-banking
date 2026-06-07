package com.internet.banking.microservice.account.service.impl;

import com.internet.banking.microservice.account.client.ManagerClient;
import com.internet.banking.microservice.account.dao.AccountRepository;
import com.internet.banking.microservice.account.data.DeleteManagerEventData;
import com.internet.banking.microservice.account.data.ManagerData;
import com.internet.banking.microservice.account.data.TransferCompletedEventData;
import com.internet.banking.microservice.account.model.AccountModel;
import com.internet.banking.microservice.account.producer.AccountSagaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountSagaService implements com.internet.banking.microservice.account.service.AccountSagaService {

    private final AccountRepository repository;

    private final ManagerClient managerClient;

    private final AccountSagaProducer producer;

    public void transferAccounts(
            DeleteManagerEventData event
    ) {

        String removedManagerCpf =
                event.getCpf();

        List<ManagerData> managers =
                managerClient.getAllManagers();

        ManagerData targetManager =
                managers.stream()
                        .filter(manager ->
                                !manager.getCpf()
                                        .equals(removedManagerCpf)
                        )
                        .min(Comparator.comparingLong(
                                manager ->
                                        repository.countByCpfManager(
                                                manager.getCpf()
                                        )
                        ))
                        .orElseThrow();

        List<AccountModel> accounts =
                repository.findByCpfManager(
                        removedManagerCpf
                );

        accounts.forEach(account ->
                account.setCpfManager(
                        targetManager.getCpf()
                )
        );

        repository.saveAll(accounts);

        producer.sendTransferCompleted(
                new TransferCompletedEventData(
                        removedManagerCpf,
                        targetManager.getCpf()
                )
        );
    }
}
