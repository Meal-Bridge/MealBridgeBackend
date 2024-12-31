package com.meal_bridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Meal Bridge API")
                .version("1.0")
                .description("API documentation for the Meal Bridge application")
            )
            .addSecurityItem(new SecurityRequirement().addList("JWT"))
            .components(new Components().addSecuritySchemes("JWT", new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
            ));
    }
}
