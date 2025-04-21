package com.boardservice.entity;

import com.boardservice.client.UserClient;
import com.boardservice.dto.CommentDTO;
import com.common.dto.UsersInfoDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
@Entity
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int comment_id;

    @Column(name = "board_id")
    private int boardId;

    @Column(name = "id")
    private int id;

    private String comment;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));



    public static CommentEntity toEntity(CommentDTO dto) {
        return CommentEntity.builder()
                .comment_id(dto.getComment_id())
                .boardId(dto.getBoard_id())
                .id(dto.getId())
                .comment(dto.getComment())
                .date(dto.getDate())
                .build();
    }

    public static List<CommentDTO> ToDtoList(List<CommentEntity> entities, UserClient userClient) {
        List<CommentDTO> dtos = new ArrayList<>();
        for (CommentEntity entity : entities) {
            CommentDTO dto = new CommentDTO();
            dto.setComment_id(entity.getComment_id());
            dto.setBoard_id(entity.getBoardId());

            // 👇 유저 정보 호출
            UsersInfoDTO user = userClient.getUserInfo(entity.getId());
            dto.setId(user.getId());
            dto.setProfile_img(user.getProfile_img());
            dto.setNickname(user.getNickname());

            dto.setComment(entity.getComment());
            dto.setDate(entity.getDate());

            dtos.add(dto);
        }
        return dtos;
    }
}

