package com.osrsflip.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI osrsGeFlipperOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OSRS GE Flipper API")
                        .description("REST API for Grand Exchange flipping opportunities. " +
                                "Fetches live prices from the OSRS Wiki and scores items by flip potential.")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token")));
    }
}
