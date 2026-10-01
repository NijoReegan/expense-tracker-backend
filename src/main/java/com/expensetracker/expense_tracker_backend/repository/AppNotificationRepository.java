package com.expensetracker.expense_tracker_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.expensetracker.expense_tracker_backend.entity.AppNotification;

public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {

    List<AppNotification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<AppNotification> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndTitleAndMessage(Long userId, String title, String message);

    @Modifying
    @Query("update AppNotification n set n.read = true where n.user.id = :userId and n.read = false")
    int markAllRead(@Param("userId") Long userId);
}