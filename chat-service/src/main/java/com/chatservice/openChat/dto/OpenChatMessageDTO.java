package com.chatservice.openChat.dto;

import com.chatservice.openChat.entity.OpenChatMessageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
@Schema(description = "커뮤니티 채팅 메시지 DTO")
public class OpenChatMessageDTO {
    @Schema(description = "커뮤니티 채팅 메시지 ID",example = "123")
    int openMessageId;

    @Schema(description = "커뮤니티 채팅방 ID",example = " 13")
    int openChatId;

    @Schema(description = "채팅 메세지",example = "안녕하세요 반가워요.")
    String content;

    @Schema(description = "메세지 입력한 사람의 ID",example = "12")
    int id;

    @Schema(description = "입력한 사람의 nickName",example = "nick")
    String nickname;

    @Schema(description = "입력한 사람의 프로필 사진",example = "www.s3dasd.jpg")
    String profile_img;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

    public static List<OpenChatMessageDTO> ToDtoList(List<OpenChatMessageEntity> entities) {
        List<OpenChatMessageDTO> dtos = new ArrayList<>();
        for (OpenChatMessageEntity entity : entities) {
            OpenChatMessageDTO dto = new OpenChatMessageDTO();
            dto.setOpenMessageId(entity.getOpenMessageId());
            dto.setOpenChatId(entity.getOpenChatId());
//            dto.setId(entity.getUser().getId());
//            dto.setProfile_img(entity.getUser().getProfile_img());
//            dto.setNickname(entity.getUser().getNickname());
            dto.setContent(entity.getContent());
            dto.setDate(entity.getDate());
            dtos.add(dto);
        }
        return dtos;
    }

    public static OpenChatMessageDTO toDTO(OpenChatMessageEntity dto) {
        return OpenChatMessageDTO.builder()
                .openMessageId(dto.getOpenMessageId())
                .openChatId(dto.getOpenChatId())
//                .id(dto.getUser().getId())
//                .nickname(dto.getUser().getNickname())
//                .profile_img(dto.getUser().getProfile_img())
                .content(dto.getContent())
                .date(dto.getDate())
                .build();
    }

}
