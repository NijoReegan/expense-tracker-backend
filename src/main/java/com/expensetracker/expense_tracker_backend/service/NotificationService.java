package com.expensetracker.expense_tracker_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expensetracker.expense_tracker_backend.dto.request.NotificationRequest;
import com.expensetracker.expense_tracker_backend.dto.response.NotificationResponse;
import com.expensetracker.expense_tracker_backend.dto.response.OkResponse;
import com.expensetracker.expense_tracker_backend.entity.AppNotification;
import com.expensetracker.expense_tracker_backend.entity.User;
import com.expensetracker.expense_tracker_backend.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker_backend.repository.AppNotificationRepository;

@Service
public class NotificationService {

    private final AppNotificationRepository notificationRepository;

    public NotificationService(AppNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationResponse> list(User user) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    public NotificationResponse create(User user, NotificationRequest request) {
        AppNotification notification = new AppNotification();
        notification.setUser(user);
        notification.setTitle(request.title().trim());
        notification.setMessage(request.message().trim());
        notification.setIcon(request.icon());
        notification.setIconBg(request.iconBg());
        notification.setRead(Boolean.TRUE.equals(request.read()));
        return NotificationResponse.from(notificationRepository.save(notification));
    }

    @Transactional
    public OkResponse markRead(User user, Long id) {
        AppNotification notification = notificationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found."));
        notification.setRead(true);
        return new OkResponse(true);
    }

    @Transactional
    public OkResponse markAllRead(User user) {
        notificationRepository.markAllRead(user.getId());
        return new OkResponse(true);
    }
}