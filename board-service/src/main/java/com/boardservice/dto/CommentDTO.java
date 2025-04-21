package com.boardservice.dto;


import com.boardservice.entity.CommentEntity;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "댓글 DTO")
public class CommentDTO {

    @Schema(description = "댓글 ID", example = "1")
    private int comment_id;

    @Schema(description = "게시글 ID", example = "123")
    private int board_id;

    @Schema(description = "유저 ID", example = "100")
    private int id;

    @Schema(description = "사용자 닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "사용자 프로필 이미지 경로", example = "이미지 경로")
    private String profile_img;

    @Schema(description = "댓글 내용", example = "이것은 댓글 내용입니다.")
    private String comment;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

    public static CommentDTO toDTO(CommentEntity entity) {
        return CommentDTO.builder()
                .comment_id(entity.getComment_id())
                .board_id(entity.getBoardId())
//                .id(entity.getUser().getId())
//                .nickname(entity.getUser().getNickname())
//                .profile_img(entity.getUser().getProfile_img())
                .comment(entity.getComment())
                .date(entity.getDate())
                .build();
    }
}
