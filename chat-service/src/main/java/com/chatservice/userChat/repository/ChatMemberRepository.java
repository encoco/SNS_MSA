package com.chatservice.userChat.repository;

import com.chatservice.userChat.entity.ChatMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMemberRepository extends JpaRepository<ChatMemberEntity, Integer> {
    List<ChatMemberEntity> findByChatRoom_Id(int chatRoomId);
    List<ChatMemberEntity> findByUserId(int userId);

    void deleteByChatRoom_IdAndUserId(int userChatId, int id);
}

