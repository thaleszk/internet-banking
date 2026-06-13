package com.internet.banking.gateway.filter;

import com.internet.banking.gateway.security.InvalidatedTokenStore;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LogoutTokenFilter implements WebFilter {

    private final InvalidatedTokenStore invalidatedTokenStore;

    public LogoutTokenFilter(InvalidatedTokenStore invalidatedTokenStore) {
        this.invalidatedTokenStore = invalidatedTokenStore;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange);
        String path = exchange.getRequest().getPath().value();

        if (isLogout(exchange)) {
            invalidatedTokenStore.add(token);
            return chain.filter(exchange);
        }

        if (!isPublicRequest(exchange) && invalidatedTokenStore.contains(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private boolean isLogout(ServerWebExchange exchange) {
        return HttpMethod.POST.equals(exchange.getRequest().getMethod())
                && "/logout".equals(exchange.getRequest().getPath().value());
    }

    private boolean isPublicPath(String path) {
        return "/login".equals(path) || "/logout".equals(path) || "/reboot".equals(path);
    }

    private boolean isPublicRequest(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        return isPublicPath(path)
                || (HttpMethod.POST.equals(exchange.getRequest().getMethod()) && "/clientes".equals(path));
    }

    private String extractToken(ServerWebExchange exchange) {
        String authorization = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authorization == null || authorization.isBlank()) {
            return "";
        }
        return authorization.replaceFirst("(?i)^Bearer\\s+", "").trim();
    }
}
