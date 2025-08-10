package com.example.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CreateNotificationRequest;
import com.example.demo.dto.NotificationResponse;
import com.example.demo.dto.UpdateNotificationRequest;
import com.example.demo.model.Notification;
import com.example.demo.service.NotificationService;

// REST controller for Notification API (MySQL only)
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Create notification
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@RequestBody CreateNotificationRequest request) {
        Notification notification = notificationService.createNotification(
                request.type(),
                request.recipient(),
                request.subject(),
                request.content());
        NotificationResponse response = toResponse(notification);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get notification by ID
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        Optional<Notification> optional = notificationService.getNotificationById(id);
        if (optional.isPresent()) {
            return ResponseEntity.ok(toResponse(optional.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all notifications (for now, as recent API)
    @GetMapping("/recent")
    public ResponseEntity<List<NotificationResponse>> getRecentNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        List<NotificationResponse> responses = notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Update notification
    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> updateNotification(@PathVariable Long id,
            @RequestBody UpdateNotificationRequest request) {
        Optional<Notification> optional = notificationService.updateNotification(id, request.subject(),
                request.content());
        if (optional.isPresent()) {
            return ResponseEntity.ok(toResponse(optional.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        boolean deleted = notificationService.deleteNotification(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Convert Notification entity to NotificationResponse DTO
    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getContent(),
                notification.getCreatedAt());
    }
}
