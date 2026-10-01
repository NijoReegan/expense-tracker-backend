package com.expensetracker.expense_tracker_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.expensetracker.expense_tracker_backend.dto.request.LoginRequest;
import com.expensetracker.expense_tracker_backend.dto.request.RegisterRequest;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.DuplicateResourceException;
import com.expensetracker.expense_tracker_backend.exception.UnauthorizedException;
import com.expensetracker.expense_tracker_backend.repository.AppNotificationRepository;
import com.expensetracker.expense_tracker_backend.repository.UserRepository;
import com.expensetracker.expense_tracker_backend.security.JwtService;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AppNotificationRepository notificationRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesUserWithHashedPasswordAndWelcomeNotification() {
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken("jane@example.com")).thenReturn("token-abc");

        var response = authService.register(new RegisterRequest("Jane Doe", "Jane@Example.com ", "secret123"));

        assertEquals("token-abc", response.token());
        assertEquals("jane@example.com", response.user().email());
        verify(passwordEncoder).encode("secret123");
        verify(notificationRepository).save(any());
    }

    @Test
    void registerWithDuplicateEmailRejects() {
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> authService.register(new RegisterRequest("Jane", "jane@example.com", "secret123")));
        verify(userRepository, never()).save(any());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void loginWithUnknownEmailRejects() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("ghost@example.com", "whatever")));
    }

    @Test
    void loginWithWrongPasswordRejects() {
        User user = new User();
        user.setEmail("jane@example.com");
        user.setPassword("$2a$10$stored");
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$10$stored")).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> authService.login(new LoginRequest("jane@example.com", "wrong")));
    }

    @Test
    void loginWithValidCredentialsReturnsToken() {
        User user = new User();
        user.setEmail("jane@example.com");
        user.setPassword("$2a$10$stored");
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "$2a$10$stored")).thenReturn(true);
        when(jwtService.generateToken("jane@example.com")).thenReturn("token-abc");

        var response = authService.login(new LoginRequest("jane@example.com", "secret123"));

        assertTrue(response.token().equals("token-abc"));
    }
}