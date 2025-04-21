package com.chatservice.userChat.entity;


import com.chatservice.userChat.dto.ChatDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Entity
@Table(name = "chat_room")
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private int id;

    @ToString.Exclude
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMemberEntity> participants = new ArrayList<>();

    @Column(name="id")
    private int userId;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));


    public static ChatRoomEntity toEntity(ChatDTO dto) {
        return ChatRoomEntity.builder()
                .id(dto.getUserChatId())
                .userId(dto.getId())
                .date(dto.getDate())
                .build();
    }
}
