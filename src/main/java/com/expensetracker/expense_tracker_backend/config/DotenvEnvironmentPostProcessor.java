package com.expensetracker.expense_tracker_backend.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Loads a local {@code .env} file (current working directory) as a low-priority
 * fallback property source, so real environment variables always win. On hosted
 * platforms no {@code .env} file exists and the platform env vars are used
 * unchanged.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DotenvEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path dotenv = Path.of(".env");
        if (!Files.isRegularFile(dotenv)) {
            return;
        }
        Map<String, Object> props = new HashMap<>();
        try {
            for (String line : Files.readAllLines(dotenv)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, eq).trim();
                String value = trimmed.substring(eq + 1).trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    props.put(key, value);
                }
            }
        } catch (IOException e) {
            log.warn("Could not read .env: {}", e.getMessage());
            return;
        }
        if (!props.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource("dotenv", props));
            log.info("Loaded {} value(s) from .env (real environment variables take precedence)", props.size());
        }
    }
}
