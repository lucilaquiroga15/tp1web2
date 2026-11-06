package com.example.demo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadata general que aparece en Swagger UI (título, descripción, versión)
 * y esquema de seguridad JWT. La documentación de cada endpoint puntual va
 * con @Operation en el controller correspondiente.
 */
@Configuration
public class OpenApiConfig {

    public static final String ESQUEMA_JWT = "bearerAuth";

    @Bean
    public OpenAPI demoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TP3 · Catálogo, Favoritos y Listas con seguridad")
                        .description("Catálogo de productos (público) + favoritos y listas persistidos en "
                                + "PostgreSQL. Los favoritos y las listas requieren autenticación con JWT: "
                                + "registrate y logueate en /api/auth, y usá el botón Authorize con el token.")
                        .version("v3"))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_JWT, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}