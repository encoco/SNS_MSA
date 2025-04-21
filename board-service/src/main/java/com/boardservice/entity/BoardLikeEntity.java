package com.boardservice.entity;


import com.boardservice.dto.BoardLikeDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "boardlike")
@Entity
public class BoardLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardlike_id;
    @Column(name = "id")
    private int userId;

    @Column(name = "board_id")
    private int boardId;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

    public static BoardLikeEntity toEntity(BoardLikeDTO dto) {
        return BoardLikeEntity.builder().boardlike_id(dto.getBoardlike_id()).boardId(dto.getBoard_id()).userId(dto.getId())
                .date(dto.getDate()).build();
    }
}
