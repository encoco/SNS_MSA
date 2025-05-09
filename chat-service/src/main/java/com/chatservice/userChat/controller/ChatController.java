package com.chatservice.userChat.controller;


import com.chatservice.userChat.docs.ChatDocs;
import com.chatservice.userChat.dto.ChatDTO;
import com.chatservice.userChat.dto.ChatMessageDTO;
import com.chatservice.userChat.service.ChatService;
import com.common.security.AuthInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController implements ChatDocs {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 채팅 리스트 반환
    @MessageMapping("/chat/{roomId}")
    public void handleMessage(@Payload ChatMessageDTO message,
                              @DestinationVariable int roomId,
                               Principal principal) {
        int userId = Integer.parseInt(principal.getName());
        message.setId(userId);

        ChatMessageDTO savedMessage = chatService.saveChat(message);
        messagingTemplate.convertAndSend("/api/chats/sub/chat/" + roomId, savedMessage);
    }

    @GetMapping("/UserMessage")
    public ResponseEntity<?> getMessage(@RequestParam(value = "userChatId") int userChatId) {
        List<ChatMessageDTO> dto = chatService.getMessage(userChatId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/rooms/my")
    public ResponseEntity<?> getMyChatRooms() {
        int userId = AuthInfoUtil.getUserId();
        long start = System.currentTimeMillis();

        Map<String, Object> dto = chatService.selectRoom(userId);
        System.out.println("채팅방 목록 : " + dto);

        long end = System.currentTimeMillis();
        System.out.println("N+1 최적화 방식 수행 시간(ms): " + (end - start));

        try {
            if (dto != null) {
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.ok("채팅방 없음");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("selectRoom error");
        }
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> createChatRoom(@RequestBody Map<String, Object> requestData) {
        try {
            int userId = AuthInfoUtil.getUserId();
            List<Integer> userIds = (List<Integer>) requestData.get("id");
            userIds.add(userId);
            Map<String, Object> response = new HashMap<>();

            ChatDTO dto = chatService.findOrCreateRoom(userIds, userId);
            if (dto.isNew_room()) {
                response.put("0", dto);
                return ResponseEntity.ok(response);
            }
            response.put("1", dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("selectRoom error");
        }
    }

    @PostMapping("/SharePost")
    public ResponseEntity<?> sharePost(@RequestBody Map<String, Object> requestData) {
        int userId = AuthInfoUtil.getUserId();
        List<Integer> userIds = (List<Integer>) requestData.get("Ids");
        int board_id = (Integer) requestData.get("board_id");

        userIds.add(userId);
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(userId);
        dto.setShare_board_id(board_id);
        ChatDTO cdto = chatService.findRoom(userIds, userId);

//        if (cdto != null) { // 찾아서 있으면 이미 있으니까 그 방으로 넘기기
//            dto.setRoom_number(cdto.getRoomNumber());
//            chatService.saveChat(dto);
//            messagingTemplate.convertAndSend("/api/sub/chat/" + cdto.getRoomNumber(), dto);
//            return ResponseEntity.ok(null);
//        }
//
//        // 위에서 return 안되면 없으니까 생성하고 생성한 정보 넘기기
//        ChatDTO Chat_DTO = chatService.createRoom(userIds, userId);
//        dto.setRoom_number(Chat_DTO.getRoomNumber());
//        chatService.saveChat(dto);
//        messagingTemplate.convertAndSend("/api/sub/chat/" + Chat_DTO.getRoomNumber(), dto);

        return ResponseEntity.ok(null);
    }
//
//    @GetMapping("/findRoom")
//    public ResponseEntity<?> findRoom(@RequestParam("id") int id) {
//        int userId = AuthInfoUtil.getUserId();
//        return ResponseEntity.ok(chatService.findOrCreateRoom(Collections.singletonList(id), userId));
//    }

    @PostMapping("/room/leave")
    public ResponseEntity<?> leaveChat(@RequestBody ChatDTO dto) {
        dto.setId(AuthInfoUtil.getUserId());

        chatService.leaveChatRoom(dto);
        return ResponseEntity.ok(null);
    }
}