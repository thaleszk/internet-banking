package com.internet.banking.customer.microservice.mapper;

import com.internet.banking.customer.microservice.data.AddressData;
import com.internet.banking.customer.microservice.model.AddressModel;

import static java.util.Objects.isNull;

public class AddressMapper {

    public static AddressModel toModel(AddressData data) {
        if (isNull(data)) {
            return null;
        }
        AddressModel model = new AddressModel();

        model.setStreetName(data.getStreetName());
        model.setStreetNumber(data.getStreetNumber());
        model.setComplement(data.getComplement());
        model.setCity(data.getCity());
        model.setState(data.getState());
        model.setZipCode(data.getZipCode());

        return model;
    }

    public static AddressData toData(AddressModel model) {
        if (isNull(model)) {
            return null;
        }
        AddressData data = new AddressData();

        data.setStreetName(model.getStreetName());
        data.setStreetNumber(model.getStreetNumber());
        data.setComplement(model.getComplement());
        data.setCity(model.getCity());
        data.setState(model.getState());
        data.setZipCode(model.getZipCode());

        return data;
    }
}
