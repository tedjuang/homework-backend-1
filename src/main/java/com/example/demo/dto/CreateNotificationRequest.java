package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(
                @NotBlank(message = "type is required") @Pattern(regexp = "^(sms|email)$", message = "type must be 'sms' or 'email'") String type,
                @NotBlank(message = "recipient is required") @Size(max = 100, message = "recipient must be at most 100 characters") String recipient,
                @NotBlank(message = "subject is required") @Size(max = 200, message = "subject must be at most 200 characters") String subject,
                @NotBlank(message = "content is required") @Size(max = 2000, message = "content must be at most 2000 characters") String content) {
}
