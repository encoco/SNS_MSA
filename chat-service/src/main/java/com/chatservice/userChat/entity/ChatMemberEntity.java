package com.chatservice.userChat.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_member")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private ChatRoomEntity chatRoom;

    @Column(name = "user_id")
    private int userId;

    private String date; // 참여 날짜 등

}
