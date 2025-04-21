package com.chatservice.userChat.repository;


import com.chatservice.userChat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {

    List<ChatMessageEntity> findByChatRoom_Id(int chatId);
}
