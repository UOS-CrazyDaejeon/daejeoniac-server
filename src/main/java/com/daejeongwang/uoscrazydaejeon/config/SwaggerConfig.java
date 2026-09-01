package com.daejeongwang.uoscrazydaejeon.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT";

        return new OpenAPI()
                .servers(List.of(new Server().url("https://api.daejeoniac.site")))
//                .servers(List.of(new Server().url("http://localhost:8080")))
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                                .name(jwtSchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(jwtSchemeName))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("UOS 대전광")
                .description("대전광 API 명세서")
                .version("1.0.0");
    }
}
