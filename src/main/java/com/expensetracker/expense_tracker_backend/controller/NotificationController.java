package com.expensetracker.expense_tracker_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.expense_tracker_backend.dto.request.NotificationRequest;
import com.expensetracker.expense_tracker_backend.dto.response.NotificationResponse;
import com.expensetracker.expense_tracker_backend.dto.response.OkResponse;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.security.CurrentUserResolver;
import com.expensetracker.expense_tracker_backend.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserResolver currentUserResolver;

    public NotificationController(NotificationService notificationService,
                                  CurrentUserResolver currentUserResolver) {
        this.notificationService = notificationService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list() {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(notificationService.list(user));
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@Valid @RequestBody NotificationRequest request) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(user, request));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<OkResponse> markRead(@PathVariable Long id) {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(notificationService.markRead(user, id));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<OkResponse> markAllRead() {
        User user = currentUserResolver.requireCurrentUser();
        return ResponseEntity.ok(notificationService.markAllRead(user));
    }
}