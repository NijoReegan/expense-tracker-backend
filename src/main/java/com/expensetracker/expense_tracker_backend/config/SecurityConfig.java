package com.expensetracker.expense_tracker_backend.config;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.expensetracker.expense_tracker_backend.dto.response.ApiErrorResponse;
import com.expensetracker.expense_tracker_backend.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ObjectMapper objectMapper;
    private final boolean exposeApiDocs;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CorsConfigurationSource corsConfigurationSource,
                          ObjectMapper objectMapper,
                          @Value("${app.expose-api-docs:true}") boolean exposeApiDocs) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.objectMapper = objectMapper;
        this.exposeApiDocs = exposeApiDocs;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, ex) ->
                                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                                        "Authentication is required to access this resource.", request.getRequestURI()))
                        .accessDeniedHandler((request, response, ex) ->
                                writeJson(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden",
                                        "You do not have permission to perform this action.", request.getRequestURI())))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/register", "/auth/login", "/auth/logout").permitAll()
                            .requestMatchers("/health", "/error").permitAll();
                    if (exposeApiDocs) {
                        auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
                    }
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void writeJson(HttpServletResponse response, int status, String error,
                           String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                new ApiErrorResponse(Instant.now(), status, error,
                        message != null ? message : HttpStatus.valueOf(status).getReasonPhrase(), path));
    }
}