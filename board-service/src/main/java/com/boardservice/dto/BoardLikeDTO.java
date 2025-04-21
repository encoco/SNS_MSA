package com.boardservice.dto;


import com.boardservice.entity.BoardLikeEntity;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "게시글 좋아요 DTO")
public class BoardLikeDTO {

    @Schema(description = "게시글 좋아요 ID", example = "1")
    private int boardlike_id;

    @Schema(description = "유저 ID", example = "100")
    private int id;

    @Schema(description = "게시글 ID", example = "123")
    private int board_id;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

    public static BoardLikeDTO toDTO(BoardLikeEntity entity) {
        return BoardLikeDTO.builder()
                .boardlike_id(entity.getBoardlike_id())
                .board_id(entity.getBoardId())
                .id(entity.getUserId())
                .date(entity.getDate())
                .build();
    }

    public static List<BoardLikeDTO> ToDtoList(List<BoardLikeEntity> entities) {
        List<BoardLikeDTO> dtos = new ArrayList<>();
        for (BoardLikeEntity entity : entities) {
            BoardLikeDTO dto = new BoardLikeDTO();
            dto.setBoard_id(entity.getBoardId());
            dto.setId(entity.getUserId());
            dto.setDate(entity.getDate());
            dtos.add(dto);
        }
        return dtos;
    }
}
