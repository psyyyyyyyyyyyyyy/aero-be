package com.sw.aero.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Swagger API 명세서")
                        .version("1.0")
                        .description("""
                                # 2025 SW AERO 프로젝트
                                
                                ## 주의사항
                                - 딱히 없음
                                
                                ## 문의
                                - 기술 문의: tjddus0731@naver.com
                                - 일반 문의: tjddus0731@naver.com
                                """)
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

    @Bean
    public GroupedOpenApi customGroupedOpenApi() {
        return GroupedOpenApi.builder().group("api").pathsToMatch("/**").build();
    }

    @Bean
    public GroupedOpenApi devApi() {
        return GroupedOpenApi.builder().group("api-dev").pathsToMatch("/api/dev/**").build();
    }
}
