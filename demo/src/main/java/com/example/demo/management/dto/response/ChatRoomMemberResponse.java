package com.example.demo.management.dto.response;

import com.example.demo.enums.ChatRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomMemberResponse {
    private Long userId;
    private ChatRole role;
    private Boolean muted;
    private Boolean pinned;
}
