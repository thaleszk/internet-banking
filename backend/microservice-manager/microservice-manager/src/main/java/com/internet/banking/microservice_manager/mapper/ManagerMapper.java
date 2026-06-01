package com.internet.banking.microservice_manager.mapper;

import com.internet.banking.microservice_manager.data.ManagerData;
import com.internet.banking.microservice_manager.model.ManagerModel;

public class ManagerMapper {

    public static ManagerModel toModel(ManagerData data) {
        if (data == null) return null;

        ManagerModel model = new ManagerModel();
        model.setName(data.getName());
        model.setEmail(data.getEmail());
        model.setCpf(data.getCpf());
        model.setPhone(data.getPhone());
        return model;
    }

    public static ManagerData toData(ManagerModel model) {
        if (model == null) return null;

        ManagerData data = new ManagerData();
        data.setName(model.getName());
        data.setEmail(model.getEmail());
        data.setCpf(model.getCpf());
        data.setPhone(model.getPhone());
        return data;
    }
}