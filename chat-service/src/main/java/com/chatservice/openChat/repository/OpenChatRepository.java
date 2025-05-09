package com.chatservice.openChat.repository;

import com.chatservice.openChat.dto.OpenChatDTO;
import com.chatservice.openChat.entity.OpenChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface OpenChatRepository extends JpaRepository<OpenChatEntity, Integer> {
    List<OpenChatEntity> findByOpenChatIdIn(Set<Integer> openChatIds);
}
