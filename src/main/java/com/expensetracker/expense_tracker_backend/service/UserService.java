package com.expensetracker.expense_tracker_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expensetracker.expense_tracker_backend.dto.request.ChangePasswordRequest;
import com.expensetracker.expense_tracker_backend.dto.response.OkResponse;
import com.expensetracker.expense_tracker_backend.dto.response.UserResponse;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.BadRequestException;
import com.expensetracker.expense_tracker_backend.exception.DuplicateResourceException;
import com.expensetracker.expense_tracker_backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse me(User user) {
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(User user, String fullName, String email) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            user.setFullName(fullName.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            String normalized = email.trim().toLowerCase();
            if (!normalized.equals(user.getEmail())) {
                if (userRepository.existsByEmail(normalized)) {
                    throw new DuplicateResourceException("An account with this email already exists.");
                }
                user.setEmail(normalized);
            }
        }
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateSettings(User user, String currency, String language,
                                       Boolean darkMode, Boolean glass) {
        if (currency != null && !currency.trim().isEmpty()) {
            user.setCurrency(currency.trim());
        }
        if (language != null && !language.trim().isEmpty()) {
            user.setLanguage(language.trim());
        }
        if (darkMode != null) {
            user.setDarkMode(darkMode);
        }
        if (glass != null) {
            user.setGlass(glass);
        }
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public OkResponse changePassword(User user, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return new OkResponse(true);
    }
}