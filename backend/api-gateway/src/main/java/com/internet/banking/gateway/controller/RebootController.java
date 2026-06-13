package com.internet.banking.gateway.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RebootController {

    @GetMapping("/reboot")
    public ResponseEntity<Map<String, String>> reboot() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Ambiente pronto para os testes"
        ));
    }
}
