package com.chatservice.userChat.repository;


import com.chatservice.userChat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatRoomEntity, Integer> {
    void deleteByIdAndUserId(int id, int userId);
}


