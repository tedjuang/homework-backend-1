package com.example.demo.dto;

public record CreateNotificationRequest(
        String type,
        String recipient,
        String subject,
        String content) {
}

