package com.chatservice.openChat.dto;

import com.chatservice.openChat.entity.OpenChatEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "커뮤니티 채팅 DTO")
public class OpenChatDTO {
    @Schema(description = "커뮤니티 채팅 ID", example = "1")
    private int openChatId;

    @Schema(description = "사용자 ID", example = "1")
    private int id;

    @Schema(description = "채팅방 제목", example = "놀사람 구해요")
    private String title;

    @Schema(description = "채팅방 설명", example = "자유로운 의견을 나누는 채팅방입니다.")
    private String description;

    @Schema(description = "이미지 경로", example = "www.s3sdasda.jpg")
    private String imgpath;

    @Schema(description = "업로드할 이미지 파일")
    private MultipartFile img;

    @Schema(description = "참여자 수", example = "5")
    private int participantCount;

    private List<Integer> memberIds;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

    public static OpenChatDTO toDTO(OpenChatEntity entity) {
        return OpenChatDTO.builder()
                .openChatId(entity.getOpenChatId())
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .imgpath(entity.getImg())
                .date(entity.getDate())
                .build();
    }

    public static List<OpenChatDTO> toDTOList(List<OpenChatEntity> entitis) {
        List<OpenChatDTO> dtos = new ArrayList<>();
        for (OpenChatEntity entity : entitis) {
            OpenChatDTO dto = new OpenChatDTO();
            dto.setOpenChatId(entity.getOpenChatId());
            dto.setId(entity.getId());
            dto.setTitle(entity.getTitle());
            dto.setDescription(entity.getDescription());
            dto.setImgpath(entity.getImg());
            dto.setDate(entity.getDate());
            dtos.add(dto);
        }
        return dtos;
    }
}
