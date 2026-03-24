package com.internet.banking.customer.microservice.mapper;

import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.model.CustomerModel;

import static java.util.Objects.isNull;

public class CustomerMapper {

    public static CustomerModel toModel(CustomerData data) {
        if (isNull(data)) {
            return null;
        }
        CustomerModel model = new CustomerModel();

        model.setName(data.getName());
        model.setEmail(data.getEmail());
        model.setCpf(data.getCpf());
        model.setPhone(data.getPhone());
        model.setSalary(data.getSalary());
        model.setAddress(AddressMapper.toModel(data.getAddress()));

        return model;
    }

    public static CustomerData toData(CustomerModel model) {
        if (isNull(model)) {
            return null;
        }
        CustomerData data = new CustomerData();

        data.setName(model.getName());
        data.setEmail(model.getEmail());
        data.setCpf(model.getCpf());
        data.setPhone(model.getPhone());
        data.setSalary(model.getSalary());
        data.setAddress(AddressMapper.toData(model.getAddress()));

        return data;
    }
}
