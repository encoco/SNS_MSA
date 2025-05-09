package com.chatservice.userChat.dto;


import com.chatservice.userChat.entity.ChatMessageEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "일반채팅 메시지 DTO" )
public class ChatMessageDTO {

    @Schema(description = "일반채팅 메세지 ID", example = "1")
    private int message_id;

    @Schema(description = "채팅방 ID", example = "123")
    private int chat_id;

    @Schema(description = "유저 ID", example = "1")
    private int id;

    @Schema(description = "채팅 메세지 내용", example = "ㅎㅇㅎㅇ")
    private String content;

    @Schema(description = "닉네임", example = "nick")
    private String nickname;

    @Schema(description = "프로필 사진", example = "프로필사진 주소")
    private String profile_img;

    @Schema(description = "채팅방에 공유한 게시물 ID", example = "766")
    private Integer share_board_id;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

    public static ChatMessageDTO toDTO(ChatMessageEntity entity, String nickname, String profileImg) {
        return ChatMessageDTO.builder()
                .message_id(entity.getMessage_id())
                .chat_id(entity.getChatRoom().getId())
                .id(entity.getUserId())
                .content(entity.getContent())
                .share_board_id(entity.getShare_board_id())
                .nickname(nickname)
                .profile_img(profileImg)
                .date(entity.getDate())
                .build();
    }

    public static List<ChatMessageDTO> toDtoList(List<ChatMessageEntity> entities,
                                                 Map<Integer, String> nicknames,
                                                 Map<Integer, String> profileImgs) {
        List<ChatMessageDTO> dtos = new ArrayList<>();
        for (ChatMessageEntity entity : entities) {
            int userId = entity.getUserId();
            int roomId = entity.getChatRoom().getId();

            String nickname = nicknames.getOrDefault(userId, "");
            String profileImg = profileImgs.getOrDefault(userId, "");

            ChatMessageDTO dto = ChatMessageDTO.builder()
                    .message_id(entity.getMessage_id())
                    .chat_id(roomId)
                    .id(userId)
                    .nickname(nickname)
                    .profile_img(profileImg)
                    .content(entity.getContent())
                    .date(entity.getDate())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

}