package com.example.demo.management.controller;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.management.dto.response.ChatRoomListResponse;
import com.example.demo.management.model.ChatRoom;
import com.example.demo.management.service.ChatRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @Operation(
            summary = "Get my chat rooms",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('CHAT_ROOM_LIST')")
    @GetMapping
    public List<ChatRoomListResponse> getMyRooms() {
        Long myUserId = CurrentUserUtils.getUserId();

        return chatRoomService.getMyRooms(myUserId);
    }

    @Operation(
            summary = "Get room detail",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "404", description = "Not found")
            }
    )
    @PreAuthorize("hasAuthority('CHAT_ROOM_DETAIL')")
    @GetMapping("/{roomId}")
    public ChatRoom getRoom(@PathVariable UUID roomId) {
        Long myUserId = CurrentUserUtils.getUserId();

        return chatRoomService.getRoom(roomId, myUserId);
    }
}