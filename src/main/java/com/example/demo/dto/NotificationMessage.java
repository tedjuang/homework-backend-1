package com.example.demo.dto;

import java.io.Serializable;

public class NotificationMessage implements Serializable {
    private String type;
    private String recipient;
    private String subject;
    private String content;

    public NotificationMessage() {
    }

    public NotificationMessage(String type, String recipient, String subject, String content) {
        this.type = type;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "type='" + type + '\'' +
                ", recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
