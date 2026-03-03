package com.example.demo.management.service;

import com.example.demo.enums.ChatRoomType;
import com.example.demo.management.dto.response.ChatRoomListResponse;
import com.example.demo.management.model.ChatRoom;
import com.example.demo.management.model.ChatRoomMember;
import com.example.demo.management.repository.ChatRoomRepository;
import com.example.demo.management.repository.ChatRoomMemberRepository;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupingRepository;

    public List<ChatRoomListResponse> getMyRooms(Long myUserId) {

        List<ChatRoomMember> memberships = chatRoomMemberRepository.findAllByUserId(myUserId);

        return memberships.stream()
                .map(m -> chatRoomRepository.findById(m.getRoomId()).orElseThrow())
                .map(room -> mapRoom(room, myUserId))
                .toList();
    }

    private ChatRoomListResponse mapRoom(ChatRoom room, Long myUserId) {

        String title = room.getTitle();
        String image = null;

        if (room.getType() == ChatRoomType.GROUP) {
            if (room.getGroupId() != null) {
                var group = groupingRepository.findById(room.getGroupId())
                        .orElseThrow(() -> new RuntimeException("Group not found"));

                title = group.getName();
//                image = group.getImage(); // group photo url
            }
        }

        else if (room.getType() == ChatRoomType.DIRECT) {
            ChatRoomMember other = chatRoomMemberRepository
                    .findOtherMember(room.getId(), myUserId)
                    .orElseThrow(() -> new RuntimeException("Direct receiver not found"));

            var receiver = userRepository.findById(other.getUserId())
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            title = receiver.getFullName();
            image = receiver.getImage(); // user photo url
        }

        return new ChatRoomListResponse(
                room.getId(),
                room.getType(),
                title,
                room.getGroupId(),
                image
        );
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