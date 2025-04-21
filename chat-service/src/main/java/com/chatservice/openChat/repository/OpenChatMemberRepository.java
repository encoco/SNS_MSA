package com.chatservice.openChat.repository;

import com.chatservice.openChat.entity.OpenChatMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpenChatMemberRepository extends JpaRepository<OpenChatMemberEntity, Integer> {
    List<OpenChatMemberEntity> findAllById(int userId);
    void deleteByOpenChatIdAndId(int openChatId, int id);

}
