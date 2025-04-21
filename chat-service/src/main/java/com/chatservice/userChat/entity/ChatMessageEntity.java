package com.chatservice.userChat.entity;


import com.chatservice.userChat.dto.ChatMessageDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@Table(name = "chat_message")
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int message_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private ChatRoomEntity chatRoom;

    @Column(name = "id") // 유저 ID만 저장
    private int userId;

    private String content;

    private String date;

    private Integer share_board_id;

    public static ChatMessageEntity toEntity(ChatMessageDTO dto) {
        ChatRoomEntity chat = ChatRoomEntity.builder().id(dto.getChat_id()).build();

        return ChatMessageEntity.builder()
                .message_id(dto.getMessage_id())
                .chatRoom(chat)
                .userId(dto.getId())
                .share_board_id(dto.getShare_board_id())
                .content(dto.getContent())
                .date(dto.getDate())
                .build();
    }
}
