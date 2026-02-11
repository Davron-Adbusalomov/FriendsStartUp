package com.example.demo.management.controller;

import com.example.demo.config.CurrentUserUtils;
import com.example.demo.config.TenantContext;
import com.example.demo.management.dto.request.SendMessageRequest;
import com.example.demo.management.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(
            summary = "Get badge",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PreAuthorize("hasAuthority('SEND_MESSAGE')")
    @PostMapping("/send")
    public UUID sendMessage(@RequestBody SendMessageRequest request) {
        Long senderId = CurrentUserUtils.getUserId();
        UUID centerId = TenantContext.getCenterId();

        return chatService.sendMessage(senderId, centerId, request);
    }
}