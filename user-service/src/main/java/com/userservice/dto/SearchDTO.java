package com.userservice.dto;

import com.userservice.entity.UsersEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Schema(description = "검색해서 나온 유저의 기본 정보")
public class SearchDTO {
    @Schema(description = "유저Key", example = "1")
    private int id;

    @Schema(description = "닉네임", example = "nick")
    private String nickname;

    @Schema(description = "프로필 사진", example = "www.S3wsadjlasbdg.jpg")
    private String img;

    public static List<SearchDTO> toSearchDTO(List<UsersEntity> entity) {
        List<SearchDTO> dtos = new ArrayList<>();
        for (UsersEntity ent : entity) {
            SearchDTO dto = new SearchDTO();
            dto.setId(ent.getId());
            dto.setNickname(ent.getNickname());
            dto.setImg(ent.getProfile_img());
            dtos.add(dto);
        }
        return dtos;
    }

    public static SearchDTO toDTO(UsersEntity entity) {
        SearchDTO dto = new SearchDTO();
        dto.setId(entity.getId());
        dto.setNickname(entity.getNickname());
        dto.setImg(entity.getProfile_img());
        return dto;
    }
}
