package com.expensetracker.expense_tracker_backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

class JwtServiceTest {

    private static final String SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";

    @Test
    void generatesAndValidatesTokenForSubject() {
        JwtService jwtService = new JwtService(SECRET, 3600_000L);
        String token = jwtService.generateToken("jane@example.com");

        assertEquals("jane@example.com", jwtService.extractEmail(token));

        UserDetails details = User.withUsername("jane@example.com").password("x").roles("USER").build();
        assertTrue(jwtService.isTokenValid(token, details));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService jwtService = new JwtService(SECRET, -1000L);
        String token = jwtService.generateToken("jane@example.com");
        UserDetails details = User.withUsername("jane@example.com").password("x").roles("USER").build();

        assertTrue(!jwtService.isTokenValid(token, details));
    }

    @Test
    void tamperedTokenIsRejected() {
        JwtService jwtService = new JwtService(SECRET, 3600_000L);
        String token = jwtService.generateToken("jane@example.com");
        UserDetails details = User.withUsername("jane@example.com").password("x").roles("USER").build();

        String tampered = token.substring(0, token.length() - 4) + "AAAA";
        assertTrue(!jwtService.isTokenValid(tampered, details));
    }

    @Test
    void tokenForAnotherUserIsRejected() {
        JwtService jwtService = new JwtService(SECRET, 3600_000L);
        String token = jwtService.generateToken("alice@example.com");
        UserDetails details = User.withUsername("bob@example.com").password("x").roles("USER").build();

        assertTrue(!jwtService.isTokenValid(token, details));
    }

    @Test
    void constructorRejectsShortSecret() {
        assertThrows(IllegalStateException.class, () -> new JwtService("short", 1000L));
    }
}