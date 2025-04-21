package com.chatservice.openChat.entity;

import com.chatservice.openChat.dto.OpenChatDTO;
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
@Table(name = "open_chat_member")
@AllArgsConstructor
@NoArgsConstructor
public class OpenChatMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_member_id")
    private int openMemberId;

    @Column(name = "open_chat_id")
    private int openChatId;

    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_chat_id", insertable = false, updatable = false)
    private OpenChatEntity openChat;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

    public static OpenChatMemberEntity toEntity(OpenChatDTO dto) {
        return OpenChatMemberEntity.builder()
                .openChatId(dto.getOpenChatId())
                .id(dto.getId())
                .date(dto.getDate())
                .build();
    }
}
