package com.internet.banking.microservice.account.client;

import com.internet.banking.microservice.account.data.ManagerData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "manager-service",
        url = "${manager.service.url}"
)
public interface ManagerClient {

    @GetMapping("/managers")
    List<ManagerData> getAllManagers();
}