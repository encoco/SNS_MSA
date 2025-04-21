package com.userservice.dto;


import com.userservice.entity.UsersEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "유저 정보 프런트 저장을 위해 비밀번호는 숨긴 DTO")
public class UsersInfoDTO {
    @Schema(description = "기본키",example = "1")
    int id;
    @Schema(description = "닉네임",example = "nick")
    String nickname;
    @Schema(description = "프로필 사진",example = "www.S3~~!~!~!@~sdad.jpg")
    String img;

    @Schema(description = "상태 메세지",example = "안녕 반갑")
    String state_message;

    @Schema(description = "전화번호",example = "01013241324")
    String phone;

    @Schema(description = "Email",example = "fqhfh@naver.com")
    String email;

    @Schema(description = "사용자 권한 ",example = "ROLE_USER")
    String role;

    public static UsersInfoDTO toInfoDTO(UsersEntity entity) {
        return UsersInfoDTO.builder().id(entity.getId()).nickname(entity.getNickname()).img(entity.getProfile_img())
                .state_message(entity.getState_message()).phone(entity.getPhone()).email(entity.getEmail()).build();
    }

    public static UsersInfoDTO toInfoDTO(UsersDTO entity) {
        return UsersInfoDTO.builder()
                .id(entity.getId())
                .nickname(entity.getNickname())
                .img(entity.getProfile_img())
                .state_message(entity.getState_message())
                .phone(entity.getPhone())
                .email(entity.getEmail()).build();
    }


}
