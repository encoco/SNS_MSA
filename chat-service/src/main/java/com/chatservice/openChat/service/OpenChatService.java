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

import java.util.*;
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

    public void CreateOpenChat(OpenChatDTO dto) {
        OpenChatEntity entity = OpenChatEntity.toEntity(dto);
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

    public void joinOpenRoom(OpenChatDTO dto) {
        OpenChatMemberEntity entity = OpenChatMemberEntity.toEntity(dto);
        openMemberRepository.save(entity);
    }

    public Map<String, Object> selectMyOpenRoomsGrouped(int userId) {
        Set<Integer> openChatIds = getJoinedOpenChatIds(userId);
        if (openChatIds.isEmpty()) {
            return createEmptyResult();
        }

        Map<Integer, List<Integer>> memberMap = getMemberMap(openChatIds);
        List<UsersInfoDTO> userInfoList = getUserInfoList(memberMap);
        List<OpenChatDTO> openRooms = getOpenChatDTOs(openChatIds, memberMap);

        Map<String, Object> result = new HashMap<>();
        result.put("openRooms", openRooms);
        result.put("userInfoList", userInfoList);
        return result;
    }

    public OpenChatMessageDTO saveOpenChat(OpenChatMessageDTO message) {
        OpenChatMessageEntity entity = OpenChatMessageEntity.toEntity(message);
        openMessageRepository.save(entity);
        return message;
    }

    public List<OpenChatMessageDTO> getOpenMessage(int openChatId) {
        List<OpenChatMessageEntity> entities = openMessageRepository.findByOpenChatId(openChatId);
        List<Long> userIds =  entities.stream()
                .map(entity -> (long) entity.getUserId())
                .distinct()
                .collect(Collectors.toList());

        List<UsersInfoDTO> userInfoList = userClient.getUserInfoList(userIds).getBody();
        Map<Long, UsersInfoDTO> userInfoMap = userInfoList.stream()
                .collect(Collectors.toMap(
                        dto -> (long) dto.getId(),
                        dto -> dto
                ));

        List<OpenChatMessageDTO> dtos = new ArrayList<>();
        for (OpenChatMessageEntity entity : entities) {
            OpenChatMessageDTO dto = new OpenChatMessageDTO();
            dto.setOpenMessageId(entity.getOpenMessageId());
            dto.setOpenChatId(entity.getOpenChatId());
            dto.setId(entity.getUserId());
            dto.setContent(entity.getContent());
            dto.setDate(entity.getDate());

            UsersInfoDTO userInfo = userInfoMap.get((long) entity.getUserId());
            if (userInfo != null) {
                dto.setNickname(userInfo.getNickname());
                dto.setProfile_img(userInfo.getProfile_img());
            }

            dtos.add(dto);
        }
        return dtos;
    }

    @Transactional
    public void leaveOpenRoom(OpenChatMemberDTO dto) {
        openMemberRepository.deleteByOpenChatIdAndId(dto.getOpenChatId(),dto.getId());
    }


    private Set<Integer> getJoinedOpenChatIds(int userId) {
        List<OpenChatMemberEntity> myMemberships = openMemberRepository.findAllById(userId);
        return myMemberships.stream()
                .map(m -> m.getOpenChat().getOpenChatId())
                .collect(Collectors.toSet());
    }

    private Map<Integer, List<Integer>> getMemberMap(Set<Integer> openChatIds) {
        List<OpenChatMemberEntity> allMembers = openMemberRepository.findByOpenChat_OpenChatIdIn(openChatIds);
        return allMembers.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getOpenChat().getOpenChatId(),
                        Collectors.mapping(OpenChatMemberEntity::getId, Collectors.toList())
                ));
    }

    private List<UsersInfoDTO> getUserInfoList(Map<Integer, List<Integer>> memberMap) {
        Set<Integer> allIds = memberMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        List<Long> userIdList = allIds.stream()
                .map(Integer::longValue)
                .toList();
        return userClient.getUserInfoList(userIdList).getBody();
    }

    private List<OpenChatDTO> getOpenChatDTOs(Set<Integer> openChatIds, Map<Integer, List<Integer>> memberMap) {
        List<OpenChatEntity> openChatEntities = openChatRepository.findByOpenChatIdIn(openChatIds);
        return openChatEntities.stream()
                .map(entity -> {
                    OpenChatDTO dto = OpenChatDTO.toDTO(entity);
                    dto.setMemberIds(memberMap.getOrDefault(entity.getOpenChatId(), List.of()));
                    return dto;
                })
                .toList();
    }

    private Map<String, Object> createEmptyResult() {
        Map<String, Object> emptyResult = new HashMap<>();
        emptyResult.put("openRooms", Collections.emptyList());
        emptyResult.put("userInfoList", Collections.emptyList());
        return emptyResult;
    }
}
