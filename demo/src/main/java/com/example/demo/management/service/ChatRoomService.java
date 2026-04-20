package com.example.demo.management.service;

import com.example.demo.config.TenantContext;
import com.example.demo.enums.ChatRole;
import com.example.demo.enums.ChatRoomType;
import com.example.demo.management.dto.response.ChatRoomListResponse;
import com.example.demo.management.model.ChatRoom;
import com.example.demo.management.model.ChatRoomMember;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.repository.ChatRoomMemberRepository;
import com.example.demo.management.repository.ChatRoomRepository;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupingRepository;

    public List<ChatRoomListResponse> getMyRooms(Long myUserId) {

        List<ChatRoomMember> memberships =
                chatRoomMemberRepository.findAllByUserId(myUserId);

        List<UUID> roomIds = memberships.stream()
                .map(ChatRoomMember::getRoomId)
                .toList();

        List<ChatRoom> rooms =
                chatRoomRepository.findAllByIdIn(roomIds);

        return rooms.stream()
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
        } else if (room.getType() == ChatRoomType.DIRECT) {
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

    @Transactional
    public ChatRoom createRoomForGroup(UUID groupId, Boolean enable) {

        Grouping group = groupingRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Optional<ChatRoom> existing =
                chatRoomRepository.findAnyByGroupId(groupId);

        if (enable) {
            if (existing.isPresent()) {
                chatRoomRepository.setActive(groupId);
                return existing.get();
            } else {
                return createChatRoomForGroup(groupId, group);
            }

        } else {
            if (existing.isEmpty()) {
                throw new RuntimeException("Chat room not found");
            }
            chatRoomRepository.setPassive(groupId);
            return existing.get();
        }
    }

    private ChatRoom createChatRoomForGroup(UUID groupId, Grouping group) {
        ChatRoom room = new ChatRoom();
        room.setType(ChatRoomType.GROUP);
        room.setGroupId(groupId);
        room.setTitle(group.getName());
        room.setCenterId(TenantContext.getCenterId());
        chatRoomRepository.saveAndFlush(room);
        if (room.getId() == null) {
            throw new RuntimeException("Room ID is NULL!");
        }

        List<ChatRoomMember> members = group.getStudents().stream().map(student -> {
            ChatRoomMember m = new ChatRoomMember();
            m.setUserId(student.getId());
            m.setRoomId(room.getId());
            m.setCenterId(room.getCenterId());
            m.setRole(ChatRole.MEMBER);
            return m;
        }).toList();
        chatRoomMemberRepository.saveAll(members);
        return room;
    }
}