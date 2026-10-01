package com.expensetracker.expense_tracker_backend.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Unauthenticated liveness/readiness probe used by the hosting platform
 * (Render health check path). Reports database reachability without leaking
 * connection details.
 */
@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        boolean databaseUp;
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            databaseUp = true;
        } catch (Exception e) {
            databaseUp = false;
        }

        Map<String, Object> body = Map.of(
                "status", databaseUp ? "UP" : "DEGRADED",
                "database", databaseUp ? "UP" : "DOWN",
                "timestamp", Instant.now().toString());

        return databaseUp
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(503).body(body);
    }
}
