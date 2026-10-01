package com.expensetracker.expense_tracker_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.expense_tracker_backend.dto.request.ChangePasswordRequest;
import com.expensetracker.expense_tracker_backend.dto.request.UpdateProfileRequest;
import com.expensetracker.expense_tracker_backend.dto.request.UpdateSettingsRequest;
import com.expensetracker.expense_tracker_backend.dto.response.OkResponse;
import com.expensetracker.expense_tracker_backend.dto.response.UserResponse;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.security.CurrentUserResolver;
import com.expensetracker.expense_tracker_backend.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserResolver currentUserResolver;

    public UserController(UserService userService, CurrentUserResolver currentUserResolver) {
        this.userService = userService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(userService.me(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(userService.updateProfile(user, request.fullName(), request.email()));
    }

    @PutMapping("/me/settings")
    public ResponseEntity<UserResponse> updateSettings(@Valid @RequestBody UpdateSettingsRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(userService.updateSettings(user, request.currency(), request.language(),
                request.darkMode(), request.glass()));
    }

    @PutMapping("/me/password")
    public ResponseEntity<OkResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(userService.changePassword(user, request));
    }
}