package com.example.demo.management.service;

import com.example.demo.management.dto.response.ChatRoomMemberResponse;
import com.example.demo.management.model.ChatRoomMember;
import com.example.demo.management.repository.ChatRoomMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomMemberService {

    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public List<ChatRoomMemberResponse> getRoomMembers(UUID roomId, Long myUserId) {

        boolean member = chatRoomMemberRepository.existsByRoomIdAndUserId(roomId, myUserId);
        if (!member) {
            throw new RuntimeException("You are not member of this room");
        }

        return chatRoomMemberRepository.findAllByRoomId(roomId)
                .stream()
                .map(m -> new ChatRoomMemberResponse(
                        m.getUserId(),
                        m.getRole(),
                        m.getMuted(),
                        m.getPinned()
                ))
                .toList();
    }

    @Transactional
    public void pinRoom(UUID roomId, Long myUserId, boolean pinned) {

        ChatRoomMember member = chatRoomMemberRepository.findByRoomIdAndUserId(roomId, myUserId)
                .orElseThrow(() -> new RuntimeException("You are not member of this room"));

        member.setPinned(pinned);
        chatRoomMemberRepository.save(member);
    }

    @Transactional
    public void muteRoom(UUID roomId, Long myUserId, boolean muted) {

        ChatRoomMember member = chatRoomMemberRepository.findByRoomIdAndUserId(roomId, myUserId)
                .orElseThrow(() -> new RuntimeException("You are not member of this room"));

        member.setMuted(muted);
        chatRoomMemberRepository.save(member);
    }

    @Transactional
    public void markAsRead(UUID roomId, Long myUserId, UUID lastMessageId) {

        ChatRoomMember member = chatRoomMemberRepository.findByRoomIdAndUserId(roomId, myUserId)
                .orElseThrow(() -> new RuntimeException("You are not member of this room"));

        member.setLastReadMessageId(lastMessageId);
        chatRoomMemberRepository.save(member);
    }
}