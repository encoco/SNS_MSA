package com.chatservice.userChat.dto;

import lombok.Data;

@Data
public class ChatMemberDTO {
    private int userId;
    private String nickname;
    private String profileImg;
}