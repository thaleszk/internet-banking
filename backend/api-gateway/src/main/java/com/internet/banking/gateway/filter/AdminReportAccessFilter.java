package com.internet.banking.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class AdminReportAccessFilter implements WebFilter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        if (isRestrictedAdminCustomerReport(exchange) && "GERENTE".equals(extractRole(exchange))) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isRestrictedAdminCustomerReport(ServerWebExchange exchange) {
        return HttpMethod.GET.equals(exchange.getRequest().getMethod())
                && "/clientes".equals(exchange.getRequest().getPath().value())
                && "adm_relatorio_clientes".equals(
                        exchange.getRequest().getQueryParams().getFirst("filtro")
                );
    }

    private String extractRole(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authorization == null || authorization.isBlank()) {
            return "";
        }

    String token = authorization.replaceFirst("(?i)^Bearer\\s+", "").trim();

    String role = extractRoleFromJwt(token);
    if (!role.isBlank()) {
        return role;
    }

    return extractRoleFromLegacyToken(token);
}

private String extractRoleFromJwt(String token) {
    String[] parts = token.split("\\.");
    if (parts.length != 3) {
        return "";
    }

try {
    String payload = parts[1];
    int padding = payload.length() % 4;
    if (padding > 0) {
        payload = payload + "=".repeat(4 - padding);
    }

    String decoded = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
    Map<?, ?> data = OBJECT_MAPPER.readValue(decoded, Map.class);
    Object tipo = data.get("tipo");
    return tipo == null ? "" : tipo.toString();
} catch (Exception e) {
    return "";
}
}

private String extractRoleFromLegacyToken(String token) {
    try {
        String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        String[] parts = decoded.split(":");
        return parts.length >= 2 ? parts[1] : "";
    } catch (IllegalArgumentException e) {
        return "";
    }
}
}
