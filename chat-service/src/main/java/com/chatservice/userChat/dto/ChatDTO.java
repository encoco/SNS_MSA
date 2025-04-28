package com.chatservice.userChat.dto;


import com.chatservice.userChat.entity.ChatRoomEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "채팅방 DTO" )
public class ChatDTO {
    @Schema(description = "채팅방 ID" , example = "1")
    private int userChatId;

    @Schema(description = "유저 ID" , example = "1")
    private int id;

    @Schema(description = "채팅방 이름(참여자 이름 조합으로 채팅 목록에 뜨게 함)" , example = "류경록/홍길동/김첨지")
    private String roomname;

    @Schema(description = "참여자 프로필 사진" , example = "이미지 주소~~")
    private String profile_img;

    @Schema(description = "새로 만든 채팅방인지 생성할 때 확인용" , example = "T/F")
    private boolean new_room;

    private List<Integer> memberIds;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

    public static ChatDTO toDTO(ChatRoomEntity entity, int myId, String roomname, String profile_img) {
        return ChatDTO.builder()
                .userChatId(entity.getId())
                .id(myId)
                .roomname(roomname)
                .profile_img(profile_img)
                .date(entity.getDate())
                .build();
    }

    public static ChatDTO toDTO(ChatRoomEntity entity, List<Integer> memberIds) {
        return ChatDTO.builder()
                .userChatId(entity.getId())
                .memberIds(memberIds)
                .date(entity.getDate())
                .build();
    }

}
