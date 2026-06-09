package com.internet.banking.microservice.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internet.banking.microservice.auth.model.UserModel;
import com.internet.banking.microservice.auth.service.JwtService;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class DefaultJwtService implements JwtService {

    private static final String SECRET = "bantads-secret-key-2026";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String generateToken(UserModel user) {
        try {
            String headerJson = OBJECT_MAPPER.writeValueAsString(Map.of(
                    "alg", "HS256",
                    "typ", "JWT"
            ));

            String payloadJson = OBJECT_MAPPER.writeValueAsString(Map.of(
                    "cpf", user.getCpf(),
                    "nome", user.getNome(),
                    "email", user.getLogin(),
                    "tipo", user.getType().name(),
                    "iat", System.currentTimeMillis() / 1000
            ));

            String header = base64UrlEncode(headerJson);
            String payload = base64UrlEncode(payloadJson);
            String signature = base64UrlEncode(sign(header + "." + payload));

            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar token JWT", e);
        }
    }

    @Override
    public String extractUsername(String token) {
        Map<String, Object> payload = parsePayload(token);
        Object email = payload.get("email");
        return email == null ? "" : email.toString();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                return false;
            }

            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String expected = base64UrlEncode(sign(header + "." + payload));
            if (!expected.equals(signature)) {
                return false;
            }

            Map<String, Object> parsed = parsePayload(token);
            return parsed.containsKey("email") && parsed.containsKey("cpf");
        } catch (Exception e) {
            return false;
        }
    }

    private String base64UrlEncode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }

    private Map<String, Object> parsePayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Map.of();
            }
            String payload = parts[1];
            int padding = payload.length() % 4;
            if (padding > 0) {
                payload += "=".repeat(4 - padding);
            }
            String decoded = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            return OBJECT_MAPPER.readValue(decoded, Map.class);
        } catch (Exception e) {
            return Map.of();
        }
    }
}
