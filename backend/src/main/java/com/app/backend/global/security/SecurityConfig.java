package com.app.backend.global.security;

import com.app.backend.global.config.AppConfig;
import com.app.backend.global.redis.repository.RedisRepository;
import com.app.backend.global.rs.RsData;
import com.app.backend.global.security.filter.JwtAuthenticationFilter;
import com.app.backend.global.security.filter.JwtAuthorizationFilter;
import com.app.backend.global.security.handler.JwtLogoutHandler;
import com.app.backend.global.security.handler.JwtLogoutSuccessHandler;
import com.app.backend.global.security.util.AuthResponseUtil;
import com.app.backend.global.security.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final RedisRepository redisRepository;

    @Value("${jwt.token.access-expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${jwt.token.refresh-expiration}")
    private long REFRESH_EXPIRATION;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration configuration) throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
                jwtUtil, ACCESS_EXPIRATION, REFRESH_EXPIRATION, objectMapper, redisRepository, authenticationManager(configuration));
        jwtAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");

        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(
                jwtUtil, ACCESS_EXPIRATION, REFRESH_EXPIRATION, objectMapper, redisRepository);

        http.headers(head -> head
                        .frameOptions(option -> option.sameOrigin()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(HttpMethod.GET, "/h2-console/**", "/api/*/signup", "/api/*/products", "/api/*/products/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/*/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/*/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/*/products","/api/*/products/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/*/products","/api/*/products/*").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter, JwtAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                        {
                            AuthResponseUtil.failLogin(
                                    response, new RsData<>(false, "400", "로그인을 진행해주세요"), HttpServletResponse.SC_BAD_REQUEST, objectMapper);
                        }))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, authException) ->
                        {
                            AuthResponseUtil.failLogin(
                                    response, new RsData<>(false, "403", "권한이 없습니다"), HttpServletResponse.SC_FORBIDDEN, objectMapper);
                        }))
                .logout(logout -> logout
                        .logoutUrl("/api/v1/logout")
                        .addLogoutHandler(new JwtLogoutHandler(jwtUtil, redisRepository))
                        .logoutSuccessHandler(new JwtLogoutSuccessHandler(objectMapper)));

        return http.build();
    }


    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 허용할 오리진 설정
        configuration.setAllowedOrigins(Arrays.asList(
                "https://cdpn.io",
                AppConfig.getSiteFrontUrl(),
                "https://localhost:5173",
                "https://localhost:4173"
        ));
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        // 자격 증명 허용 설정
        configuration.setAllowCredentials(true);
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        // CORS 설정을 소스에 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
