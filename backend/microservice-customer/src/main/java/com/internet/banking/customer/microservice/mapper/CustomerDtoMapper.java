package com.internet.banking.customer.microservice.mapper;

import com.internet.banking.customer.microservice.data.AddressData;
import com.internet.banking.customer.microservice.data.CustomerData;
import com.internet.banking.customer.microservice.dto.request.AddressRequest;
import com.internet.banking.customer.microservice.dto.request.CustomerRequest;
import com.internet.banking.customer.microservice.dto.response.AddressResponse;
import com.internet.banking.customer.microservice.dto.response.CustomerResponse;

import java.util.List;

import static java.util.Objects.isNull;

public class CustomerDtoMapper {

    public static CustomerData toData(final CustomerRequest request) {
        if (isNull(request)) {
            return null;
        }

        CustomerData data = new CustomerData();
        data.setName(request.getName());
        data.setEmail(request.getEmail());
        data.setCpf(request.getCpf());
        data.setPhone(request.getPhone());
        data.setSalary(request.getSalary());
        data.setAddress(toData(request.getAddress()));

        return data;
    }

    public static CustomerResponse toResponse(final CustomerData data) {
        if (isNull(data)) {
            return null;
        }

        CustomerResponse response = new CustomerResponse();
        response.setName(data.getName());
        response.setEmail(data.getEmail());
        response.setCpf(data.getCpf());
        response.setPhone(data.getPhone());
        response.setSalary(data.getSalary());
        response.setAddress(toResponse(data.getAddress()));

        return response;
    }

    public static List<CustomerResponse> toResponseList(final List<CustomerData> customers) {
        return customers.stream()
                .map(CustomerDtoMapper::toResponse)
                .toList();
    }

    private static AddressData toData(final AddressRequest request) {
        if (isNull(request)) {
            return null;
        }

        AddressData data = new AddressData();
        data.setStreetName(request.getStreetName());
        data.setStreetNumber(request.getStreetNumber());
        data.setComplement(request.getComplement());
        data.setZipCode(request.getZipCode());
        data.setCity(request.getCity());
        data.setState(request.getState());

        return data;
    }

    private static AddressResponse toResponse(final AddressData data) {
        if (isNull(data)) {
            return null;
        }

        AddressResponse response = new AddressResponse();
        response.setStreetName(data.getStreetName());
        response.setStreetNumber(data.getStreetNumber());
        response.setComplement(data.getComplement());
        response.setZipCode(data.getZipCode());
        response.setCity(data.getCity());
        response.setState(data.getState());

        return response;
    }
}