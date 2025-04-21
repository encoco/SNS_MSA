package com.chatservice.openChat.repository;

import com.chatservice.openChat.entity.OpenChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenChatRepository extends JpaRepository<OpenChatEntity, Integer> {

}
