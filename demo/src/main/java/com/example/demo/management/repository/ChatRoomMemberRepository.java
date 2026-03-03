package com.example.demo.management.repository;

import com.example.demo.management.model.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, UUID> {

    boolean existsByRoomIdAndUserId(UUID roomId, Long userId);

    List<ChatRoomMember> findAllByUserId(Long userId);

    List<ChatRoomMember> findAllByRoomId(UUID roomId);

    Optional<ChatRoomMember> findByRoomIdAndUserId(UUID roomId, Long userId);

    @Query("""
    select m from ChatRoomMember m
    where m.roomId = :roomId
    and m.userId <> :myUserId
    """)
    Optional<ChatRoomMember> findOtherMember(UUID roomId, Long myUserId);
}