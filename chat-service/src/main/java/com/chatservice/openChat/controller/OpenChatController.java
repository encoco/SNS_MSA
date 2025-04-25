package com.chatservice.openChat.controller;

import com.chatservice.openChat.docs.OpenDocs;
import com.chatservice.openChat.dto.OpenChatDTO;
import com.chatservice.openChat.dto.OpenChatMemberDTO;
import com.chatservice.openChat.dto.OpenChatMessageDTO;
import com.chatservice.openChat.service.OpenChatService;
import com.common.security.AuthInfoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class OpenChatController implements OpenDocs {
    private final OpenChatService openChatService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/open/{openChatId}")
    @SendTo("/api/chats/sub/open/{openChatId}")
    public void openMessage(@DestinationVariable("openChatId") int chat_id, @Payload OpenChatMessageDTO message
            , Principal principal){
        int userId = Integer.parseInt(principal.getName());

        message.setOpenChatId(chat_id);
        message.setId(userId);

        OpenChatMessageDTO savedMessage = openChatService.saveCommChat(message);
        messagingTemplate.convertAndSend("/api/chats/sub/open/" + chat_id, savedMessage);

    }

    @GetMapping("/open")
    public ResponseEntity<?> openRoom() {
        List<OpenChatDTO> dto = openChatService.selectAllOpenRoom();

        try {
            if (dto != null) {
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.ok("채팅방 없음");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("selectRoom error");
        }
    }

    @GetMapping("/open/my")
    public ResponseEntity<?> selectOpenMyRoom() {
        List<OpenChatMemberDTO> dto = openChatService.selectOpenChat(AuthInfoUtil.getUserId());
        System.out.println(dto);
        try {
            if (dto != null) {
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.ok("채팅방 없음");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("selectRoom error");
        }
    }

    @PostMapping("/open")
    public ResponseEntity<?> createOpenChat(@ModelAttribute OpenChatDTO dto) {
        try {
            dto.setId(AuthInfoUtil.getUserId());
            openChatService.CreateCommChat(dto);
            return ResponseEntity.ok("");

        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("selectRoom error");
        }
    }


    @GetMapping("/open/{openChatId}/messages")
    public ResponseEntity<List<OpenChatMessageDTO>> getMessages(@PathVariable int openChatId) {
        List<OpenChatMessageDTO> dto = openChatService.getCommMessage(openChatId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/open/join")
    public ResponseEntity<?> joinOpenRoom(@RequestBody OpenChatDTO dto) {
        dto.setId(AuthInfoUtil.getUserId());
        openChatService.joinCommunity(dto);
        return ResponseEntity.ok(null);
    }


    @PostMapping("/open/{openChatId}/leave")
        public ResponseEntity<Void> leaveOpenChat(@RequestBody OpenChatMemberDTO dto) {
        dto.setId(AuthInfoUtil.getUserId());
        openChatService.leaveChatRoom(dto);
        return ResponseEntity.ok(null);
    }
}
