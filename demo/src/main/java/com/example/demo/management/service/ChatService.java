package com.example.demo.management.service;

import com.example.demo.enums.ChatRoomType;
import com.example.demo.management.dto.request.SendMessageRequest;
import com.example.demo.management.model.ChatRoom;
import com.example.demo.management.model.ChatRoomMember;
import com.example.demo.management.model.UserEntity;
import com.example.demo.management.repository.ChatRoomMemberRepository;
import com.example.demo.management.repository.ChatRoomRepository;
import com.example.demo.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;
    private final SupabaseMessageService supabaseMessageService;

    @Transactional
    public UUID sendMessage(Long senderId, UUID centerId, SendMessageRequest request) {

        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new RuntimeException("Message content is empty");
        }

        UUID roomId;

        if (request.getRoomId() != null) {
            roomId = request.getRoomId();

            boolean memberExists = chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, senderId);
            if (!memberExists) {
                throw new RuntimeException("You are not member of this room");
            }
        }

        // 2) Direct chat (receiverId)
        else if (request.getReceiverId() != null) {

            Long receiverId = request.getReceiverId();

            if (Objects.equals(senderId, receiverId)) {
                throw new RuntimeException("You cannot message yourself");
            }

            UserEntity sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            UserEntity receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            if (!sender.getCenterId().equals(receiver.getCenterId())) {
                throw new RuntimeException("Users are not in same center");
            }

            roomId = chatRoomRepository.findDirectRoomBetweenUsers(senderId, receiverId)
                    .orElseGet(() -> createDirectRoom(senderId, receiverId, centerId));
        }

        // 3) Group chat (groupId)
        else if (request.getGroupId() != null) {

            UUID groupId = request.getGroupId();

            ChatRoom room = chatRoomRepository.findByGroupId(groupId)
                    .orElseGet(() -> createGroupRoom(groupId, centerId));

            roomId = room.getId();

            boolean memberExists = chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, senderId);
            if (!memberExists) {
                throw new RuntimeException("You are not member of this group");
            }
        }

        else {
            throw new RuntimeException("roomId OR receiverId OR groupId must be provided");
        }

        // Insert into Supabase
        Map<String, Object> payload = new HashMap<>();
        payload.put("room_id", roomId);
        payload.put("sender_id", senderId);
        payload.put("center_id", centerId);
        payload.put("content", request.getContent());
        payload.put("message_type", request.getMessageType() == null ? "TEXT" : request.getMessageType());
        payload.put("created_at", Instant.now().toString());

        if (request.getReplyToMessageId() != null) {
            payload.put("reply_to_message_id", request.getReplyToMessageId());
        }

        supabaseMessageService.insertMessage(payload);

        return roomId;
    }

    private UUID createDirectRoom(Long senderId, Long receiverId, UUID centerId) {

        ChatRoom room = new ChatRoom();
        room.setType(ChatRoomType.DIRECT);
        room.setCenterId(centerId);
        room = chatRoomRepository.save(room);

        ChatRoomMember m1 = new ChatRoomMember();
        m1.setRoomId(room.getId());
        m1.setUserId(senderId);
        m1.setCenterId(centerId);

        ChatRoomMember m2 = new ChatRoomMember();
        m2.setRoomId(room.getId());
        m2.setUserId(receiverId);
        m2.setCenterId(centerId);

        chatRoomMemberRepository.saveAll(List.of(m1, m2));

        return room.getId();
    }

    private ChatRoom createGroupRoom(UUID groupId, UUID centerId) {

        ChatRoom room = new ChatRoom();
        room.setType(ChatRoomType.GROUP);
        room.setGroupId(groupId);
        room.setCenterId(centerId);

        return chatRoomRepository.save(room);
    }
}