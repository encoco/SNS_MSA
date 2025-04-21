package com.userservice.entity;


import com.userservice.dto.UsersDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "profile_img")
    private String profile_img;

    @Column(name = "state_message")
    private String state_message;

    @Column(name = "role")
    private String role;

    public static UsersEntity toEntity(UsersDTO dto) {
        return UsersEntity.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .profile_img(dto.getProfile_img())
                .state_message(dto.getState_message())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }

}
