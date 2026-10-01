package com.expensetracker.expense_tracker_backend.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    /**
     * Comma-separated list of allowed origins. Entries may be exact origins
     * (https://app.vercel.app) or Spring origin patterns (https://*.vercel.app).
     */
    @Value("${app.frontend-urls:}")
    private String frontendUrls;

    /**
     * Convenience flag for allowing every Vercel deployment URL, including the
     * per-commit preview deployments. Never enable together with credentials on
     * a public API.
     */
    @Value("${app.allow-vercel-previews:false}")
    private boolean allowVercelPreviews;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> patterns = new ArrayList<>();
        Arrays.stream(frontendUrls.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .forEach(patterns::add);
        if (allowVercelPreviews) {
            patterns.add("https://*.vercel.app");
        }

        if (patterns.isEmpty()) {
            log.warn("No CORS origins configured (app.frontend-urls is empty). "
                    + "Browser requests from a deployed frontend will be blocked.");
        } else {
            log.info("CORS allowed origins: {}", patterns);
        }

        config.setAllowedOriginPatterns(patterns);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setExposedHeaders(List.of("Location"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
