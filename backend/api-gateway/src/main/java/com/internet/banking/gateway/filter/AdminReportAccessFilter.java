package com.internet.banking.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class AdminReportAccessFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
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
        try {
            String payload = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = payload.split(":", 2);
            return parts.length == 2 ? parts[1] : "";
        } catch (IllegalArgumentException e) {
            return "";
        }
    }
}
