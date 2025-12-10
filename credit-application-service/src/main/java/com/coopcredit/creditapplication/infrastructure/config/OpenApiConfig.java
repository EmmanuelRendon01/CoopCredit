package com.coopcredit.creditapplication.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 configuration for Swagger UI documentation.
 * 
 * Configures:
 * - API information and metadata
 * - JWT Bearer token authentication scheme
 * - Security requirements for protected endpoints
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI documentation with security schemes.
     * 
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
            .info(new Info()
                .title("CoopCredit - Credit Application API")
                .description("REST API for managing credit applications, affiliates, and risk evaluations. " +
                           "Built with Hexagonal Architecture, Spring Boot, and JWT Security.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("CoopCredit Development Team")
                    .email("dev@coopcredit.com")
                    .url("https://coopcredit.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server"),
                new Server()
                    .url("http://credit-application-service:8080")
                    .description("Docker Container")
            ))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Enter JWT token obtained from /api/auth/login or /api/auth/register")));
    }
}
