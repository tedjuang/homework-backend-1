package com.example.demo.service;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.dto.NotificationMessage;

import jakarta.annotation.Resource;

@Component
public class RocketMQProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage(String topic, NotificationMessage msg) {
        System.out.println("Sending message to topic " + topic + " with message " + msg.toString());

        this.rocketMQTemplate.convertAndSend(topic, msg);
    }
}
