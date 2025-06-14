package com.sw.aero.global.config;

import com.sw.aero.global.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    //private final CustomOAuth2UserService oauth2UserService;
    //private final OAuth2LoginSuccessHandler customSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 기능 비활성화 (REST API에서는 필요없음)
                .csrf(AbstractHttpConfigurer::disable)
                // CORS 설정 활성화(보통은 CORS 설정 활성화 하지 않음. 서버에서 NginX로 CORS 검증)
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                // HTTP Basic 인증 기본 설정
                .httpBasic(Customizer.withDefaults())
                // 세션을 생성하지 않음 (JWT 사용으로 인한 Stateless 설정)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // HTTP 요청에 대한 권한 설정
                .authorizeHttpRequests(
                        request ->
                                request
                                        // 인증 없이 허용할 경로
                                        .requestMatchers(
                                                "/api/auth/**",          // 로그인 API
                                                "/swagger-ui/**",        // Swagger UI
                                                "/swagger-ui.html",      // Swagger UI 진입점
                                                "/v3/api-docs/**",       // Swagger JSON
                                                "/webjars/**",            // Swagger에서 사용하는 정적 자원
                                                "/api/courses/all",        //모든 코스 필터링 조회
                                                "/api/courses/{courseId}", // 유저코스 상세조회
                                                "/api/users",                //개발 후 없앨것
                                                "/api/tourspots/filter",    //관광지 필터링 조회
                                                "/api/tourspots/search",     //관광지명 서치
                                                "/api/plans/**"             //ai코스 상세조회
                                        ).permitAll()
                                        // 그 외 모든 요청은 모두 인증 필요
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//                .oauth2Login(oauth2 -> oauth2
//                        .userInfoEndpoint(userInfo -> userInfo
//                                .userService(oauth2UserService) // 사용자 정보 처리
//                        )
//                        .successHandler(customSuccessHandler) // 로그인 성공 처리
//                );
        return http.build();
    }

    /**
     * 비밀번호 인코더 Bean 등록
     **/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 관리자 Bean 등록
     **/
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}