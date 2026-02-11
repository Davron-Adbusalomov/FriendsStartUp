package com.example.demo.management.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class SendMessageRequest {

    private Long receiverId;

    private UUID groupId;

    private UUID roomId;

    private String content;
    private String messageType; // TEXT, IMAGE, FILE etc

    private UUID replyToMessageId;
}