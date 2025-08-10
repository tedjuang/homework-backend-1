package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;

// Service for handling Notification business logic (MySQL only)
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Create a new notification
    public Notification createNotification(String type, String recipient, String subject, String content) {
        Notification notification = new Notification(type, recipient, subject, content, LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    // Get notification by ID
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    // Get all notifications (for recent, limit can be added later)
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    // Update notification subject and content
    public Optional<Notification> updateNotification(Long id, String subject, String content) {
        Optional<Notification> optional = notificationRepository.findById(id);
        if (optional.isPresent()) {
            Notification notification = optional.get();
            notification.setSubject(subject);
            notification.setContent(content);
            notificationRepository.save(notification);
            return Optional.of(notification);
        } else {
            return Optional.empty();
        }
    }

    // Delete notification by ID
    public boolean deleteNotification(Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
