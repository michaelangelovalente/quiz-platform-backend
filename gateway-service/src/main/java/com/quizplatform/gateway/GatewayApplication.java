package com.quizplatform.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Quiz Platform API Gateway Service
 * --------------------------------------
 * Main entry point for all client requests.
 * Handles routing, cross-cutting concerns, and service orchestration.
 */
@SpringBootApplication
@RestController
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    /**
     * Gateway routing configuration
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Health check route - direct to gateway
            .route("health", r -> r.path("/health")
                .uri("forward:/gateway/health"))
            
            // Quiz service routes 
            .route("quiz-api", r -> r.path("/api/v1/quizzes/**")
                .uri("http://localhost:8081"))
            
            // Default route for any unmatched paths
            .route("default", r -> r.path("/**")
                .uri("forward:/gateway/info"))
            
            .build();
    }

    /**
     * Gateway health endpoint
     */
    @GetMapping("/gateway/health")
    public Map<String, Object> health() {
        return Map.of(
            "status", "UP",
            "service", "gateway-service",
            "timestamp", LocalDateTime.now(),
            "version", "0.0.1-SNAPSHOT"
        );
    }

    /**
     * Gateway info endpoint
     */
    @GetMapping("/gateway/info")
    public Map<String, Object> info() {
        return Map.of(
            "service", "Quiz Platform Gateway",
            "version", "0.0.1-SNAPSHOT",
            "description", "API Gateway for Quiz Platform microservices",
            "timestamp", LocalDateTime.now(),
            "availableEndpoints", Map.of(
                "health", "/health",
                "gateway-info", "/gateway/info",
                "quiz-api", "/api/v1/quizzes/**"
            )
        );
    }
}