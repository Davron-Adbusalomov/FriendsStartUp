package com.example.demo.management.model;

import com.example.demo.enums.ChatRole;
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
@Table(name = "chat_room_member")
@SQLDelete(sql = "UPDATE chat_room_member SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
@Filter(name = "centerFilter", condition = "center_id = :centerId")
public class ChatRoomMember extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;


    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private ChatRole role;

    private Boolean muted = false;

    private Boolean pinned = false;

    @Column(name = "last_read_message_id")
    private UUID lastReadMessageId;

    @Column(name = "center_id")
    private UUID centerId;

    @ManyToOne
    @JoinColumn(name = "center_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Center center;
}
