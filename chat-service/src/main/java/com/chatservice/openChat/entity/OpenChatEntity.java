package com.chatservice.openChat.entity;

import com.chatservice.openChat.dto.OpenChatDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Entity
@Table(name = "open_chat")
@AllArgsConstructor
@NoArgsConstructor
public class OpenChatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "openChatId")
    private int openChatId;
    private int id;
    private String title;
    private String description;
    private String img;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

    public static OpenChatEntity toEntity(OpenChatDTO entity) {
        return OpenChatEntity.builder()
                .openChatId(entity.getOpenChatId())
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .img(entity.getImgpath())
                .date(entity.getDate())
                .build();
    }

    public static List<OpenChatEntity> toEntityList(List<OpenChatDTO> entitis) {
        List<OpenChatEntity> dtos = new ArrayList<>();
        for (OpenChatDTO entity : entitis) {
            OpenChatEntity dto = new OpenChatEntity();
            dto.setOpenChatId(entity.getOpenChatId());
            dto.setId(entity.getId());
            dto.setTitle(entity.getTitle());
            dto.setDescription(entity.getDescription());
            dto.setImg(entity.getImgpath());
            dto.setDate(entity.getDate());
            dtos.add(dto);
        }
        return dtos;
    }

}
