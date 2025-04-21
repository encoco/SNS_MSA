package com.userservice.dto;

import com.userservice.entity.UsersEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "유저 프로필 DTO")
public class UsersDTO {

    @Schema(description = "사용자 ID", example ="1")
    private int id;

    @Schema(description = "아이디",example = "user")
    private String username;

    @Schema(description = "닉네임",example = "nick")
    private String nickname;

    @Schema(description = "비밀번호",example = "sadsad")
    private String password;

    @Schema(description = "프로필 사진",example = "www.S3~~~~.jpg")
    private String profile_img;

    @Schema(description = "상태 메세지",example = "안녕 반가워.")
    private String state_message;

    @Schema(description = "프로필 사진 저장을 위한 데이터")
    private MultipartFile imgpath;

    @Schema(description = "전화번호" ,example = "01012341234")
    private String phone;

    @Schema(description = "이메일",example = "sadjq@naver.com")
    private String email;

    @Schema(description = "이건 뭐였더라")
    private String changePassword;

    @Schema(description = "이건 뭐였더라")
    private String original;

    @Schema(description = "사용자 권한")
    @Builder.Default
    private String role = "ROLE_USER";



    public static UsersDTO toDTO(UsersEntity dto) {
        return UsersDTO.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .state_message(dto.getState_message())
                .profile_img(dto.getProfile_img())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }
}
