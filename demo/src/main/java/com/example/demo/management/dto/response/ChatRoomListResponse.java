package com.example.demo.management.dto.response;

import com.example.demo.enums.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ChatRoomListResponse {
    private UUID roomId;
    private ChatRoomType type;
    private String title;
    private UUID groupId;
    private String image;
}