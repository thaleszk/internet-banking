package com.internet.banking.manager.microservice.mapper;

import com.internet.banking.manager.microservice.data.ManagerData;
import com.internet.banking.manager.microservice.model.ManagerModel;

import static java.util.Objects.isNull;

public class ManagerMapper {

    public static ManagerModel toModel(ManagerData data) {
        if (isNull(data)) {
            return null;
        }
        ManagerModel model = new ManagerModel();

        model.setName(data.getName());
        model.setEmail(data.getEmail());
        model.setCpf(data.getCpf());
        model.setPhone(data.getPhone());

        return model;
    }

    public static ManagerData toData(ManagerModel model) {
        if (isNull(model)) {
            return null;
        }
        ManagerData data = new ManagerData();

        data.setName(model.getName());
        data.setEmail(model.getEmail());
        data.setCpf(model.getCpf());
        data.setPhone(model.getPhone());

        return data;
    }
}
