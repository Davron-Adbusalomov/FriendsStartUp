package com.example.demo.management.repository;

import com.example.demo.management.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

    @Query("""
        select r.id
        from ChatRoom r
        where r.type = com.example.demo.enums.ChatRoomType.DIRECT
        and exists (
            select 1 from ChatRoomMember m1
            where m1.roomId = r.id and m1.userId = :user1
        )
        and exists (
            select 1 from ChatRoomMember m2
            where m2.roomId = r.id and m2.userId = :user2
        )
    """)
    Optional<UUID> findDirectRoomBetweenUsers(Long user1, Long user2);

    Optional<ChatRoom> findByGroupId(UUID groupId);

    @Query("""
        select count(r) > 0
        from ChatRoom r
        where r.groupId = :groupId and r.status != 'DELETED'
    """)
    boolean existsByGroupId(UUID groupId);

    @Query("""
        select r
        from ChatRoom r
        where r.id in :roomIds and r.status != 'DELETED'
    """)
    List<ChatRoom> findAllByIdIn(List<UUID> roomIds);

    @Modifying
    @Query("""
        update ChatRoom r
        set r.status = 'DELETED'
        where r.groupId = :groupId
    """)
    void setPassive(UUID groupId);

    @Modifying
    @Query("""
        update ChatRoom r
        set r.status = 'UPDATED'
        where r.groupId = :groupId
    """)
    void setActive(UUID groupId);

    @Query("""
        select r
        from ChatRoom r
        where r.groupId = :groupId and r.status != 'DELETED'
    """)
    Optional<ChatRoom> findAnyByGroupId(UUID groupId);
}