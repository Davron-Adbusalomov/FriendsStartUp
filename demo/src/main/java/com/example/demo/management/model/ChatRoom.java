package com.example.demo.management.model;

import com.example.demo.enums.ChatRoomType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "groups")
@SQLDelete(sql = "UPDATE chat_room SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;


    @Enumerated(EnumType.STRING)
    private ChatRoomType type; // DIRECT, GROUP

    private String title; // optional for group chat

    @Column(name = "group_id")
    private UUID groupId; // if type = GROUP (Grouping.id)

    @Column(name = "center_id")
    private UUID centerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
