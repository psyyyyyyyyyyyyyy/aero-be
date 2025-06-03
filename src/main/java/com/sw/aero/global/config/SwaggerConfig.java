package com.sw.aero.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Swagger API 명세서")
                                .version("1.0")
                                .description(
                                        """
                                                # 2025 SW AERO 프로젝트
                                                
                                                ## 주의사항
                                                - 파일 업로드 크기 제한: 10MB (1개 파일 크기)
                                                
                                                ## 문의
                                                - 기술 문의: tjddus0731@naver.com
                                                - 일반 문의: tjddus0731@naver.com
                                                """));
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