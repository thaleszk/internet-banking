package com.internet.banking.microservice.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MicroserviceAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceAccountApplication.class, args);
	}

}
