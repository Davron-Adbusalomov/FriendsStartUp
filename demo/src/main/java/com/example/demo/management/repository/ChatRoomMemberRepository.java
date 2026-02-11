package com.example.demo.management.repository;

import com.example.demo.management.model.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, UUID> {

    boolean existsByRoomIdAndUserId(UUID roomId, Long userId);

    List<ChatRoomMember> findAllByUserId(Long userId);

    List<ChatRoomMember> findAllByRoomId(UUID roomId);

    Optional<ChatRoomMember> findByRoomIdAndUserId(UUID roomId, Long userId);
}