package com.example.demo.management.service;

import com.example.demo.management.dto.response.ChatRoomListResponse;
import com.example.demo.management.model.ChatRoom;
import com.example.demo.management.model.ChatRoomMember;
import com.example.demo.management.repository.ChatRoomRepository;
import com.example.demo.management.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public List<ChatRoomListResponse> getMyRooms(Long myUserId) {

        List<ChatRoomMember> memberships = chatRoomMemberRepository.findAllByUserId(myUserId);

        return memberships.stream()
                .map(m -> chatRoomRepository.findById(m.getRoomId()).orElseThrow())
                .map(r -> new ChatRoomListResponse(
                        r.getId(),
                        r.getType(),
                        r.getTitle(),
                        r.getGroupId()
                ))
                .toList();
    }

    public ChatRoom getRoom(UUID roomId, Long myUserId) {

        boolean member = chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, myUserId);
        if (!member) {
            throw new RuntimeException("You are not member of this room");
        }

        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }
}