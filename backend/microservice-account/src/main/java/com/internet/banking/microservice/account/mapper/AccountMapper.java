package com.internet.banking.microservice.account.mapper;

import com.internet.banking.microservice.account.data.AccountData;
import com.internet.banking.microservice.account.model.AccountModel;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountModel toModel(AccountData accountData) {
        if (accountData == null) {
            return null;
        }

        AccountModel accountModel = new AccountModel();
        accountModel.setCpfCustomer(accountData.getCpfCustomer());
        accountModel.setNumber(accountData.getNumber());
        accountModel.setCreationDate(accountData.getCreationDate());
        accountModel.setBalance(accountData.getBalance());
        accountModel.setLimit(accountData.getLimit());
        accountModel.setCpfManager(accountData.getCpfManager());

        return accountModel;
    }

    public AccountData toData(AccountModel accountModel) {
        if (accountModel == null) {
            return null;
        }

        AccountData accountData = new AccountData();
        accountData.setCpfCustomer(accountModel.getCpfCustomer());
        accountData.setNumber(accountModel.getNumber());
        accountData.setCreationDate(accountModel.getCreationDate());
        accountData.setBalance(accountModel.getBalance());
        accountData.setLimit(accountModel.getLimit());
        accountData.setCpfManager(accountModel.getCpfManager());

        return accountData;
    }
}