package com.magnet.ecommerce.identity_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI identityServiceOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("MagNet E-Commerce Identity Service API")
                        .version("v1")
                        .description(
                                "Authentication, authorization, user management, "
                                        + "role management, permission management, "
                                        + "account security, and identity APIs."
                        )
                        .contact(new Contact()
                                .name("MagNet E-Commerce Platform"))
                        .license(new License()
                                .name("Internal Use")))
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                );
    }
}