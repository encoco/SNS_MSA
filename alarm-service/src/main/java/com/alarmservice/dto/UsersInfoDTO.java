package com.alarmservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersInfoDTO {
    private int id;
    private String nickname;
    private String profile_img;
}