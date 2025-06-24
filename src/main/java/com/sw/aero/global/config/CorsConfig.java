package com.sw.aero.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:https://default.com}")
    private String[] allowedOrigins;

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Authorization 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Accept", "Authorization"));

        // 환경 변수에 정의된 출처만 허용
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        // 리스트에 작성한 HTTP 메소드 요청만 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 리스트에 작성한 헤더들이 포함된 요청만 허용
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Accept", "Authorization"));
        // 쿠키나 인증 정보를 포함하는 요청 허용
        configuration.setAllowCredentials(true);
        // 모든 경로에 대해 위의 CORS 설정을 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
