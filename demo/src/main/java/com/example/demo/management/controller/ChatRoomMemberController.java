package com.example.demo.management.controller;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.management.dto.response.ChatRoomMemberResponse;
import com.example.demo.management.service.ChatRoomMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/rooms/{roomId}/members")
@RequiredArgsConstructor
public class ChatRoomMemberController {

    private final ChatRoomMemberService chatRoomMemberService;

    @Operation(
            summary = "Get room members",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('CHAT_ROOM_MEMBERS')")
    @GetMapping
    public List<ChatRoomMemberResponse> getRoomMembers(@PathVariable UUID roomId) {
        Long myUserId = CurrentUserUtils.getUserId();

        return chatRoomMemberService.getRoomMembers(roomId, myUserId);
    }

    @Operation(summary = "Pin/unpin room",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @PreAuthorize("hasAuthority('CHAT_ROOM_PIN')")
    @PutMapping("/pin")
    public void pinRoom(
            @PathVariable UUID roomId,
            @RequestParam boolean pinned
    ) {
        Long myUserId = CurrentUserUtils.getUserId();
        chatRoomMemberService.pinRoom(roomId, myUserId, pinned);
    }

    @Operation(summary = "Mute/unmute room",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @PreAuthorize("hasAuthority('CHAT_ROOM_MUTE')")
    @PutMapping("/mute")
    public void muteRoom(
            @PathVariable UUID roomId,
            @RequestParam boolean muted
    ) {
        Long myUserId = CurrentUserUtils.getUserId();
        chatRoomMemberService.muteRoom(roomId, myUserId, muted);
    }

    @Operation(summary = "Mark as read",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            })
    @PreAuthorize("hasAuthority('CHAT_ROOM_READ')")
    @PutMapping("/read")
    public void markAsRead(
            @PathVariable UUID roomId,
            @RequestParam UUID lastMessageId
    ) {
        Long myUserId = CurrentUserUtils.getUserId();
        chatRoomMemberService.markAsRead(roomId, myUserId, lastMessageId);
    }
}