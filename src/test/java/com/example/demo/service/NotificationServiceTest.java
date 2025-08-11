package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import com.example.demo.dto.NotificationMessage;
import com.example.demo.model.Notification;
import com.example.demo.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RocketMQProducer rocketMQProducer;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notificationService = new NotificationService(notificationRepository, redisTemplate, objectMapper,
                rocketMQProducer);
    }

    @Test
    void testCreateNotification_success() {
        Notification saved = new Notification("email", "user@example.com", "subj", "content", LocalDateTime.now());
        saved.setId(1L);
        when(notificationRepository.save(any())).thenReturn(saved);

        Notification result = notificationService.createNotification("email", "user@example.com", "subj", "content");

        assertNotNull(result);
        assertEquals(saved.getId(), result.getId());
        verify(notificationRepository, times(1)).save(any());
        verify(rocketMQProducer, times(1)).sendMessage(eq("notification-topic"), any(NotificationMessage.class));
        verify(redisTemplate, atLeastOnce()).execute(any(), anyList(), any(), any());
    }

    @Test
    void testGetNotificationById_found() {
        Notification notification = new Notification("email", "user@example.com", "subj", "content",
                LocalDateTime.now());
        notification.setId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        Optional<Notification> result = notificationService.getNotificationById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void testGetNotificationById_notFound() {
        when(notificationRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Notification> result = notificationService.getNotificationById(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateNotification_found() {
        Notification notification = new Notification("email", "user@example.com", "old", "old", LocalDateTime.now());
        notification.setId(1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenReturn(notification);

        Optional<Notification> result = notificationService.updateNotification(1L, "newSubj", "newContent");
        assertTrue(result.isPresent());
        assertEquals("newSubj", result.get().getSubject());
        assertEquals("newContent", result.get().getContent());
        verify(notificationRepository, times(1)).save(any());
        verify(redisTemplate, atLeastOnce()).execute(any(), anyList(), any(), any());
    }

    @Test
    void testUpdateNotification_notFound() {
        when(notificationRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Notification> result = notificationService.updateNotification(2L, "subj", "content");
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteNotification_found() {
        when(notificationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificationRepository).deleteById(1L);

        boolean result = notificationService.deleteNotification(1L);
        assertTrue(result);
        verify(notificationRepository, times(1)).deleteById(1L);
        verify(redisTemplate, atLeastOnce()).execute(any(), anyList(), any());
    }

    @Test
    void testDeleteNotification_notFound() {
        when(notificationRepository.existsById(2L)).thenReturn(false);
        boolean result = notificationService.deleteNotification(2L);
        assertFalse(result);
    }
}
