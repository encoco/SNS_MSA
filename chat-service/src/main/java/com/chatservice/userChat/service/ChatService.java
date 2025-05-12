package com.chatservice.userChat.service;


import com.chatservice.client.UserClient;
import com.chatservice.userChat.dto.ChatDTO;
import com.chatservice.userChat.dto.ChatMessageDTO;
import com.chatservice.userChat.entity.ChatMemberEntity;
import com.chatservice.userChat.entity.ChatMessageEntity;
import com.chatservice.userChat.entity.ChatRoomEntity;
import com.chatservice.userChat.repository.ChatMemberRepository;
import com.chatservice.userChat.repository.ChatMessageRepository;
import com.chatservice.userChat.repository.ChatRepository;
import com.common.dto.UsersInfoDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository messageRepository;
    private final UserClient userClient;
    /**
     * 내가 참여한 채팅방 목록 조회
     */
    public Map<String, Object> selectRoom(int userId, String before) {
        List<Integer> pageRoomIds = chatRepository.findPagedRoomIds(userId, before, 30);
        if (pageRoomIds.isEmpty()) {
            return emptyResult();
        }

        List<ChatRoomEntity> chatRooms = chatRepository.findAllById(pageRoomIds);
        Map<Integer, List<ChatMemberEntity>> memberMap = findChatRoomMembers(chatRooms);
        List<UsersInfoDTO> userInfos = findParticipantInfos(memberMap, userId);

        List<ChatMessageRepository.ChatIdAndLastDate> raws =
                messageRepository.findLatestDates(pageRoomIds);
        Map<Integer, String> lastDateMap = raws.stream()
                .collect(Collectors.toMap(
                        ChatMessageRepository.ChatIdAndLastDate::getChatId,
                        ChatMessageRepository.ChatIdAndLastDate::getLastDate
                ));

        String nextCursor = null;
        if (pageRoomIds.size() == 30) {
            nextCursor = lastDateMap.get(
                    pageRoomIds.get(pageRoomIds.size() - 1)
            );
        }

        List<ChatDTO> chatDTOList = pageRoomIds.stream()
                .map(roomId -> {
                    ChatRoomEntity room = chatRooms.stream()
                            .filter(r -> r.getId() == roomId)
                            .findFirst()
                            .orElseThrow();
                    ChatDTO dto = toChatDTO(room, memberMap, userId);
                    String lastDate = lastDateMap.get(roomId);
                    dto.setLastMessageDate(
                            lastDate != null ? lastDate : room.getDate()
                    );
                    return dto;
                })
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("chatRooms", chatDTOList);
        result.put("userInfoList", userInfos);
        result.put("nextCursor", nextCursor);
        return result;
    }

    /**
     * 채팅방 생성
     */
    @Transactional
    public ChatDTO createRoom(List<Integer> userIds, int myId) {
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .userId(myId)
                .build();
        chatRepository.save(chatRoom);

        // 참여자 등록
        for (Integer userId : userIds) {
            ChatMemberEntity participant = ChatMemberEntity.builder()
                    .chatRoom(chatRoom)
                    .userId(userId)
                    .date(chatRoom.getDate())
                    .build();
            chatMemberRepository.save(participant);
        }

        // 본인도 추가
        if (!userIds.contains(myId)) {
            ChatMemberEntity selfParticipant = ChatMemberEntity.builder()
                    .chatRoom(chatRoom)
                    .userId(myId)
                    .date(chatRoom.getDate())
                    .build();
            chatMemberRepository.save(selfParticipant);
        }

        // 1:1 기준 상대방 정보 가져오기
        int partnerId = userIds.isEmpty() ? myId : userIds.get(0);
        String roomname = userClient.getNickname(partnerId);
        String profile_img = userClient.getProfileImg(partnerId);

        ChatDTO dto = ChatDTO.toDTO(chatRoom, myId, roomname, profile_img);
        dto.setNew_room(true);
        return dto;
    }

    /**
     * 주어진 유저 ID들과 정확히 일치하는 채팅방을 찾는다
     */
    public ChatDTO findRoom(List<Integer> userIds, int myId) {
        List<ChatMemberEntity> myChats = chatMemberRepository.findByUserId(myId);
        for (ChatMemberEntity participant : myChats) {
            ChatRoomEntity chatRoom = participant.getChatRoom();
            List<Integer> participantIds = chatMemberRepository.findByChatRoom_Id(chatRoom.getId())
                    .stream()
                    .map(ChatMemberEntity::getUserId)
                    .sorted()
                    .toList();

            List<Integer> input = new ArrayList<>(userIds);
            input.sort(Comparator.naturalOrder());

            if (participantIds.equals(input)) {
                int partnerId = userIds.get(0);
                String roomname = userClient.getNickname(partnerId);
                String profile_img = userClient.getProfileImg(partnerId);
                ChatDTO dto = ChatDTO.toDTO(chatRoom, myId, roomname, profile_img);
                dto.setNew_room(false);
                return dto;
            }
        }
        return null;
    }

    /**
     * 1:1 채팅방을 찾거나 없으면 새로 만든다
     */
    @Transactional
    public ChatDTO findOrCreateRoom(List<Integer> userIds, int myId) {
        System.out.println("???");
        ChatDTO existing = findRoom(userIds, myId);
        // 있으면 false  없으면 True   방 생성 하냐/ 안하냐 기준
        return (existing != null) ? existing : createRoom(userIds, myId);
    }

    /**
     * 채팅 메시지 저장
     */
    public ChatMessageDTO saveChat(ChatMessageDTO message) {
        ChatMessageEntity entity = ChatMessageEntity.toEntity(message);
        messageRepository.save(entity);

        return message;
    }

    /**
     * 채팅방 내 메시지 조회
     */
    public List<ChatMessageDTO> getMessage(int userchatId) {
        List<ChatMessageEntity> entityList = messageRepository.findByChatRoom_Id(userchatId);

        Set<Integer> userIds = entityList.stream()
                .map(ChatMessageEntity::getUserId)
                .collect(Collectors.toSet());

        Map<Integer, String> nicknames = userClient.getNicknames(new ArrayList<>(userIds));
        Map<Integer, String> profileImgs = userClient.getProfileImages(new ArrayList<>(userIds));

        return ChatMessageDTO.toDtoList(entityList, nicknames, profileImgs);
    }

    /**
     * 일반 채팅 나가기
     */
    @Transactional
    public void leaveChatRoom(ChatDTO dto) {
        chatMemberRepository.deleteByChatRoom_IdAndUserId(dto.getUserChatId(), dto.getId());
    }


    private List<ChatRoomEntity> findUserChatRooms(int userId) {
        return chatMemberRepository.findByUserId(userId).stream()
                .map(ChatMemberEntity::getChatRoom)
                .distinct()
                .toList();
    }

    private Map<Integer, List<ChatMemberEntity>> findChatRoomMembers(List<ChatRoomEntity> chatRooms) {
        List<Integer> chatRoomIds = chatRooms.stream()
                .map(ChatRoomEntity::getId)
                .toList();

        return chatMemberRepository.findByChatRoom_IdIn(chatRoomIds).stream()
                .collect(Collectors.groupingBy(m -> m.getChatRoom().getId()));
    }

    private List<UsersInfoDTO> findParticipantInfos(Map<Integer, List<ChatMemberEntity>> chatRoomMemberMap, int userId) {
        Set<Integer> participantIds = chatRoomMemberMap.values().stream()
                .flatMap(members -> members.stream()
                        .map(ChatMemberEntity::getUserId)
                        .filter(id -> id != userId)) // ✅ 나 제외
                .collect(Collectors.toSet());

        List<Long> userIdList = participantIds.stream()
                .map(Integer::longValue)
                .toList();

        return userClient.getUserInfoList(userIdList).getBody();
    }

    private ChatDTO toChatDTO(ChatRoomEntity chatRoom, Map<Integer, List<ChatMemberEntity>> chatRoomMemberMap, int userId) {
        List<Integer> memberIds = chatRoomMemberMap.getOrDefault(chatRoom.getId(), List.of()).stream()
                .map(ChatMemberEntity::getUserId)
                .filter(id -> id != userId) // ✅ 나 제외
                .toList();

        return ChatDTO.toDTO(chatRoom, memberIds);
    }

    private Map<String, Object> emptyResult() {
        Map<String, Object> empty = new HashMap<>();
        empty.put("chatRooms", Collections.emptyList());
        empty.put("userInfoList", Collections.emptyList());
        return empty;
    }
}

