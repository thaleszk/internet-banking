package com.internet.banking.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class GatewayConfig {

    @Value("${AUTH_SERVICE_URL:http://microservice-auth:8081}")
    private String authServiceUrl;

    @Value("${CUSTOMER_SERVICE_URL:http://microservice-customer:8082}")
    private String customerServiceUrl;

    @Value("${ACCOUNT_SERVICE_URL:http://microservice-account:8083}")
    private String accountServiceUrl;

    @Value("${MANAGER_SERVICE_URL:http://microservice-manager:8084}")
    private String managerServiceUrl;

    @Value("${ORCHESTRATOR_SERVICE_URL:http://microservice-orchestrator:8085}")
    private String orchestratorServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/login", "/logout", "/auth/**")
                        .filters(f -> f
                                .rewritePath("^/login$", "/auth/login")
                                .rewritePath("^/logout$", "/auth/logout"))
                        .uri(authServiceUrl))

                .route("customer-self-registration", r -> r
                        .path("/clientes")
                        .and()
                        .method("POST")
                        .filters(f -> f
                                .removeRequestHeader("Authorization")
                                .rewritePath("^/clientes$", "/customer-self-registration"))
                        .uri(orchestratorServiceUrl))

                .route("customer-service", r -> r
                        .path("/clientes", "/clientes/**", "/customers/**")
                        .filters(f -> f
                                .rewritePath("^/clientes$", "/customers")
                                .rewritePath("^/clientes/(?<segment>.*)", "/customers/${segment}"))
                        .uri(customerServiceUrl))

                .route("account-service", r -> r
                        .path("/contas", "/contas/**", "/accounts/**")
                        .filters(f -> f
                                .rewritePath("^/contas$", "/accounts")
                                .rewritePath("^/contas/(?<segment>.*)", "/accounts/${segment}"))
                        .uri(accountServiceUrl))

                .route("delete-manager", r -> r
                        .path("/manager", "/manager/**")
                        .and()
                        .method("DELETE")
                        .filters(f -> f
                                .removeRequestHeader("Authorization")
                                .rewritePath("^/manager/(?<segment>.*)$", "/manager-saga/${segment}"))
                        .uri(orchestratorServiceUrl))

                .route("manager-service", r -> r
                        .path("/gerentes", "/gerentes/**", "/managers/**")
                        .filters(f -> f
                                .rewritePath("^/gerentes$", "/managers")
                                .rewritePath("^/gerentes/(?<segment>.*)", "/managers/${segment}"))
                        .uri(managerServiceUrl))

                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:3000"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
