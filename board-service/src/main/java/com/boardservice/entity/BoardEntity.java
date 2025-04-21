package com.boardservice.entity;

import com.boardservice.dto.BoardDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardId;

    @Column(name = "id")
    private int userId;

    private String img;
    private String video;
    private String content;
    private String date;

    public static BoardEntity toEntity(BoardDTO dto) {
        return BoardEntity.builder()
                .boardId(dto.getBoard_id())
                .userId(dto.getId())
                .img(dto.getImgpath())
                .video(dto.getVideo())
                .content(dto.getContent())
                .date(dto.getDate())
                .build();
    }

    public void setUpdateContent(String content, String imgpath) {
        this.content = content;
        this.img = imgpath;
    }
}