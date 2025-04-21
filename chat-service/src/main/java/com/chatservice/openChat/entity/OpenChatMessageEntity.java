package com.chatservice.openChat.entity;


import com.chatservice.openChat.dto.OpenChatMessageDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Data
@Entity
@Table(name = "open_chat_message")
@AllArgsConstructor
@NoArgsConstructor
public class OpenChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "open_message_id")
    int openMessageId;

    @Column(name = "open_chat_id")
    int openChatId;

    @Column(name = "id")
    private int userId;

    String content;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

    public static OpenChatMessageEntity toEntity(OpenChatMessageDTO dto) {
        return OpenChatMessageEntity.builder()
                .openMessageId(dto.getOpenMessageId())
                .openChatId(dto.getOpenChatId())
                .userId(dto.getId())
                .content(dto.getContent())
                .date(dto.getDate())
                .build();
    }
}
