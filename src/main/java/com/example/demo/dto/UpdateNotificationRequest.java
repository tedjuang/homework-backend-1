package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNotificationRequest(
                @NotBlank(message = "subject is required") @Size(max = 200, message = "subject must be at most 200 characters") String subject,
                @NotBlank(message = "content is required") @Size(max = 2000, message = "content must be at most 2000 characters") String content) {
}
