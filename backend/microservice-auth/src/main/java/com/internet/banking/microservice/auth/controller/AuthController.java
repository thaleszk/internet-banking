package com.internet.banking.microservice.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internet.banking.microservice.auth.data.AuthData;
import com.internet.banking.microservice.auth.data.CreateUserData;
import com.internet.banking.microservice.auth.data.LoginData;
import com.internet.banking.microservice.auth.facade.AuthFacade;
import com.internet.banking.microservice.auth.model.UserModel;
import com.internet.banking.microservice.auth.enums.UserType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");

    private final AuthFacade authFacade;

    public AuthController(AuthFacade authFacade) {
        this.authFacade = authFacade;
    }

    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginData loginData) {
        try {
            AuthData response = authFacade.login(loginData);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // POST /auth/logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        Map<String, Object> payload = decodeTokenPayload(extractToken(authorization));

        return ResponseEntity.ok(Map.of(
                "cpf", String.valueOf(payload.getOrDefault("cpf", "")),
                "nome", String.valueOf(payload.getOrDefault("nome", "")),
                "email", String.valueOf(payload.getOrDefault("email", "")),
                "tipo", String.valueOf(payload.getOrDefault("tipo", ""))
        ));
    }

    // GET /auth/validate?token=...
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean valido = authFacade.validateToken(token);
        return ResponseEntity.ok(Map.of("valido", valido));
    }

    @PostMapping("/users/clientes")
    public ResponseEntity<?> createClientUser(@RequestBody CreateUserData request) {
        try {
            UserModel user = authFacade.createUser(
                    request.cpf(),
                    request.email(),
                    request.senha(),
                    UserType.CLIENTE,
                    request.nome()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "cpf", user.getCpf(),
                            "email", user.getLogin(),
                            "tipo", user.getType().name()
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/users/gerentes")
    public ResponseEntity<?> createManagerUser(@RequestBody CreateUserData request) {
        try {
            UserModel user = authFacade.createUser(
                    request.cpf(),
                    request.email(),
                    request.senha(),
                    UserType.GERENTE,
                    request.nome()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "cpf", user.getCpf(),
                            "email", user.getLogin(),
                            "tipo", user.getType().name()
                    ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", e.getMessage()));
        }
    }


    private Map<String, Object> decodeTokenPayload(String token) {
        if (token == null || token.isBlank()) {
            return Map.of();
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return Map.of();
        }

        String payload = parts[1];
        int padding = payload.length() % 4;
        if (padding > 0) {
            payload = payload + "=".repeat(4 - padding);
        }

        try {
            String decoded = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            return new ObjectMapper().readValue(decoded, Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }

    private String extractToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return "";
        }
        return authorization.trim().replaceFirst("(?i)^Bearer\\s+", "").replace("\"", "").trim();
    }
}
