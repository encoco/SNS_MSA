package com.chatservice.openChat.service;


import com.chatservice.client.UserClient;
import com.chatservice.openChat.dto.OpenChatDTO;
import com.chatservice.openChat.dto.OpenChatMemberDTO;
import com.chatservice.openChat.dto.OpenChatMessageDTO;
import com.chatservice.openChat.entity.OpenChatEntity;
import com.chatservice.openChat.entity.OpenChatMemberEntity;
import com.chatservice.openChat.entity.OpenChatMessageEntity;
import com.chatservice.openChat.repository.OpenChatMemberRepository;
import com.chatservice.openChat.repository.OpenChatMessageRepository;
import com.chatservice.openChat.repository.OpenChatRepository;
import com.common.dto.UsersInfoDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OpenChatService {
    private final OpenChatRepository openChatRepository;
    private final OpenChatMemberRepository openMemberRepository;
    private final OpenChatMessageRepository openMessageRepository;
    private final UserClient userClient;
    private final OpenChatMemberRepository openChatMemberRepository;
    //private final BoardService boardService;

    public void CreateCommChat(OpenChatDTO dto) {
        OpenChatEntity entity = OpenChatEntity.toEntity(dto);
        System.out.println("CreateCommChat Entity : " + entity);
        openChatRepository.save(entity);
    }

    public List<OpenChatDTO> selectAllOpenRoom() {
        List<OpenChatEntity> entityList = openChatRepository.findAll();
        List<OpenChatDTO> dtoList = new ArrayList<>();

        for (OpenChatEntity entity : entityList) {
            OpenChatDTO dto = OpenChatDTO.toDTO(entity);

            int count = openChatMemberRepository.countByOpenChatId(entity.getOpenChatId());
            dto.setParticipantCount(count);

            dtoList.add(dto);
        }

        return dtoList;
    }

    public void joinCommunity(OpenChatDTO dto) {
        OpenChatMemberEntity entity = OpenChatMemberEntity.toEntity(dto);
        openMemberRepository.save(entity);
    }

    public List<OpenChatMemberDTO> selectOpenChat(int userIdFromToken) {
        return OpenChatMemberDTO.toDTOList(openMemberRepository.findAllById(userIdFromToken));
    }

    public OpenChatMessageDTO saveCommChat(OpenChatMessageDTO message) {
        OpenChatMessageEntity entity = OpenChatMessageEntity.toEntity(message);
        openMessageRepository.save(entity);
        return message;
    }

    public List<OpenChatMessageDTO> getCommMessage(int openChatId) {
        List<OpenChatMessageEntity> entities = openMessageRepository.findByOpenChatId(openChatId);
        List<Long> userIds =  entities.stream()
                .map(entity -> (long) entity.getUserId())
                .distinct()
                .collect(Collectors.toList());

        List<UsersInfoDTO> userInfoList = userClient.getUserInfoList(userIds).getBody();
        System.out.println("userInfoList : " + userInfoList);
        Map<Long, UsersInfoDTO> userInfoMap = userInfoList.stream()
                .collect(Collectors.toMap(
                        dto -> (long) dto.getId(),
                        dto -> dto
                ));
        System.out.println("userInfoMap : " + userInfoMap);

        List<OpenChatMessageDTO> dtos = new ArrayList<>();
        for (OpenChatMessageEntity entity : entities) {
            OpenChatMessageDTO dto = new OpenChatMessageDTO();
            dto.setOpenMessageId(entity.getOpenMessageId());
            dto.setOpenChatId(entity.getOpenChatId());
            dto.setId(entity.getUserId());
            dto.setContent(entity.getContent());
            dto.setDate(entity.getDate());

            UsersInfoDTO userInfo = userInfoMap.get((long) entity.getUserId());
            System.out.println("userInfo?? : " + userInfo);
            if (userInfo != null) {
                dto.setNickname(userInfo.getNickname());
                dto.setProfile_img(userInfo.getProfile_img());
            }

            dtos.add(dto);
        }
        System.out.println("최종 dots : " + dtos);
        return dtos;
    }


    @Transactional
    public void leaveChatRoom(OpenChatMemberDTO dto) {
        openMemberRepository.deleteByOpenChatIdAndId(dto.getOpenChatId(),dto.getId());
    }
}
