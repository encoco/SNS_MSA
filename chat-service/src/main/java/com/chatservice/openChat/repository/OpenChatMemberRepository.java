package com.chatservice.openChat.repository;

import com.chatservice.openChat.entity.OpenChatMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OpenChatMemberRepository extends JpaRepository<OpenChatMemberEntity, Integer> {
    List<OpenChatMemberEntity> findAllById(int userId);
    void deleteByOpenChatIdAndId(int openChatId, int id);

    int countByOpenChatId(int openChatId);

    List<OpenChatMemberEntity> findByOpenChat_OpenChatIdIn(Set<Integer> openChatIds);

}
