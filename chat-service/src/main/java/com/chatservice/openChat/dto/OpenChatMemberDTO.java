package com.chatservice.openChat.dto;

import com.chatservice.openChat.entity.OpenChatMemberEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "오픈채팅 참여자를 위한 DTO")
public class OpenChatMemberDTO {
    @Schema(description = "참여자 PKey", example = "123")
    private int openChatMemberId;

    @Schema(description = "방 번호 FK(communityChat PKey)", example = "12")
    private int openChatId;

    @Schema(description = "방장이 입력한 방 이름", example = "수다떨사람?")
    private String title;

    @Schema(description = "참여자 ID", example = " 1")
    private int id;

    @Schema(description = "방목록에 보여질 이미지", example = "www.S3오픈채팅배경사진.jpg")
    private String img;

    @Schema(description = "방 생성 날짜", example = "2025/03/18 16:15")
    private String date;


    public static List<OpenChatMemberDTO> toDTOList(List<OpenChatMemberEntity> entitis) {
        List<OpenChatMemberDTO> dtos = new ArrayList<>();
        for (OpenChatMemberEntity entity : entitis) {
            OpenChatMemberDTO dto = new OpenChatMemberDTO();
            dto.setOpenChatMemberId(entity.getOpenMemberId());
            dto.setOpenChatId(entity.getOpenChat().getOpenChatId());
            dto.setId(entity.getId());
            dto.setImg(entity.getOpenChat().getImg());
            dto.setTitle(entity.getOpenChat().getTitle());
            dto.setDate(entity.getDate());
            dtos.add(dto);
        }
        return dtos;
    }
}
