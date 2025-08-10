package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

// Service for handling Notification business logic
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String RECENT_NOTIFICATIONS_KEY = "recent_notifications";
    private static final int RECENT_NOTIFICATIONS_LIMIT = 10;

    private DefaultRedisScript<Long> updateNotificationScript;
    private DefaultRedisScript<Long> deleteNotificationScript;
    private DefaultRedisScript<Long> addAndTrimListScript;
    private DefaultRedisScript<Long> refreshListScript;

    public NotificationService(NotificationRepository notificationRepository,
            RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    // Load Lua scripts after dependencies are injected
    @PostConstruct
    public void init() {
        updateNotificationScript = new DefaultRedisScript<>();
        updateNotificationScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("lua/recent_notifications_update_item.lua")));
        updateNotificationScript.setResultType(Long.class);

        deleteNotificationScript = new DefaultRedisScript<>();
        deleteNotificationScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("lua/recent_notifications_delete_item.lua")));
        deleteNotificationScript.setResultType(Long.class);

        addAndTrimListScript = new DefaultRedisScript<>();
        addAndTrimListScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("lua/recent_notifications_add_and_trim.lua")));
        addAndTrimListScript.setResultType(Long.class);

        refreshListScript = new DefaultRedisScript<>();
        refreshListScript.setScriptSource(
                new ResourceScriptSource(new ClassPathResource("lua/recent_notifications_refresh.lua")));
        refreshListScript.setResultType(Long.class);

        // Pre-warm the recent notifications cache on application startup
        refreshRecentNotificationsCacheFromDb();
    }

    // Create a new notification
    @CachePut(value = "notification", key = "#result.id")
    public Notification createNotification(String type, String recipient, String subject, String content) {
        Notification notification = new Notification(type, recipient, subject, content, LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);

        // Atomically update recent notifications cache via Lua script
        try {
            String json = objectMapper.writeValueAsString(savedNotification);
            redisTemplate.execute(addAndTrimListScript,
                    List.of(RECENT_NOTIFICATIONS_KEY),
                    json,
                    String.valueOf(RECENT_NOTIFICATIONS_LIMIT - 1));
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing notification for Redis cache: " + e.getMessage());
        }

        return savedNotification;
    }

    // Get notification by ID
    @Cacheable(value = "notification", key = "#id")
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    // Get the latest 10 notifications, try Redis first, then MySQL
    public List<Notification> getRecentNotifications() {
        List<String> jsonList = redisTemplate.opsForList().range(RECENT_NOTIFICATIONS_KEY, 0,
                RECENT_NOTIFICATIONS_LIMIT - 1);
        if (jsonList != null && !jsonList.isEmpty()) {
            List<Notification> cachedNotifications = jsonList.stream().map(json -> {
                try {
                    return objectMapper.readValue(json, Notification.class);
                } catch (Exception e) {
                    System.err.println("Error deserializing notification from Redis cache: " + e.getMessage());
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
            if (cachedNotifications.size() < RECENT_NOTIFICATIONS_LIMIT) {
                return refreshRecentNotificationsCacheFromDb();
            }
            return cachedNotifications;
        } else {
            return refreshRecentNotificationsCacheFromDb();
        }
    }

    // Atomically refresh recent notifications cache from DB via Lua script
    private List<Notification> refreshRecentNotificationsCacheFromDb() {
        List<Notification> notificationsFromDb = notificationRepository.findTop10ByOrderByCreatedAtDesc();

        // Prepare arguments for Lua script
        List<String> notificationJsons = notificationsFromDb.stream()
                .map(notification -> {
                    try {
                        return objectMapper.writeValueAsString(notification);
                    } catch (JsonProcessingException e) {
                        System.err.println("Error serializing notification for Redis cache refresh: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        redisTemplate.execute(refreshListScript,
                List.of(RECENT_NOTIFICATIONS_KEY),
                notificationJsons.toArray());

        return notificationsFromDb;
    }

    // Update notification subject and content
    @CachePut(value = "notification", key = "#id")
    public Optional<Notification> updateNotification(Long id, String subject, String content) {
        Optional<Notification> optional = notificationRepository.findById(id);
        if (optional.isPresent()) {
            Notification notification = optional.get();
            notification.setSubject(subject);
            notification.setContent(content);
            Notification updatedNotification = notificationRepository.save(notification);

            // Update recent notifications cache via Lua script
            updateRecentNotificationInRedisList(updatedNotification);

            return Optional.of(updatedNotification);
        } else {
            return Optional.empty();
        }
    }

    // Delete notification by ID
    @CacheEvict(value = { "notification" }, key = "#id") // Keep single notification cache eviction
    public boolean deleteNotification(Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);

            // Remove from recent notifications cache via Lua script
            removeRecentNotificationFromRedisList(id);

            return true;
        } else {
            return false;
        }
    }

    // Execute Lua script to atomically update an item in the Redis List
    private void updateRecentNotificationInRedisList(Notification notification) {
        try {
            String notificationJson = objectMapper.writeValueAsString(notification);
            redisTemplate.execute(updateNotificationScript,
                    List.of(RECENT_NOTIFICATIONS_KEY),
                    String.valueOf(notification.getId()),
                    notificationJson);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing notification for Lua update: " + e.getMessage());
        }
    }

    // Execute Lua script to atomically remove an item from the Redis List
    private void removeRecentNotificationFromRedisList(Long id) {
        redisTemplate.execute(deleteNotificationScript,
                List.of(RECENT_NOTIFICATIONS_KEY),
                String.valueOf(id));
    }
}
