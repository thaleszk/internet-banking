package com.internet.banking.microservice.account.service;

import com.internet.banking.microservice.account.data.DeleteManagerEventData;
import org.springframework.stereotype.Service;


@Service
public interface AccountSagaService {

    void transferAccounts(DeleteManagerEventData event);
}