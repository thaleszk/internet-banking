package com.internet.banking.microservice.account.service;

import com.internet.banking.microservice.account.data.DeleteManagerEventData;

public interface AccountSagaService {

    void transferAccounts(DeleteManagerEventData event);
}
