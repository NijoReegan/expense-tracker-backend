package com.expensetracker.expense_tracker_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expensetracker.expense_tracker_backend.dto.request.LoginRequest;
import com.expensetracker.expense_tracker_backend.dto.request.RegisterRequest;
import com.expensetracker.expense_tracker_backend.dto.response.AuthResponse;
import com.expensetracker.expense_tracker_backend.dto.response.UserResponse;
import com.expensetracker.expense_tracker_backend.entity.AppNotification;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.DuplicateResourceException;
import com.expensetracker.expense_tracker_backend.exception.UnauthorizedException;
import com.expensetracker.expense_tracker_backend.repository.AppNotificationRepository;
import com.expensetracker.expense_tracker_backend.repository.UserRepository;
import com.expensetracker.expense_tracker_backend.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AppNotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       AppNotificationRepository notificationRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists.");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        User saved = userRepository.save(user);

        AppNotification welcome = new AppNotification();
        welcome.setUser(saved);
        welcome.setTitle("Welcome aboard");
        welcome.setMessage("Your Smart Expense Tracker account is ready. Add your first transaction to get started.");
        welcome.setIcon("celebration");
        welcome.setIconBg("bg-primary/10 text-primary");
        welcome.setRead(false);
        notificationRepository.save(welcome);

        return new AuthResponse(jwtService.generateToken(email), UserResponse.from(saved));
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }
        return new AuthResponse(jwtService.generateToken(email), UserResponse.from(user));
    }

    private String normalizeEmail(String email) {
        return (email == null ? "" : email).trim().toLowerCase();
    }
}