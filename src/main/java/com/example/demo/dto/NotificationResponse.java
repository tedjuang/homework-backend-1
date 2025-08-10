package com.example.demo.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String type,
        String recipient,
        String subject,
        String content,
        LocalDateTime createdAt) {
}

