package com.chatservice.userChat.docs;

import com.chatservice.userChat.dto.ChatDTO;
import com.chatservice.userChat.dto.ChatMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "채팅 API", description = "일대일 채팅 관련 REST API")
public interface ChatDocs {

    @Operation(summary = "특정 채팅방 메시지 조회", description = "userChatId(방 번호)에 해당하는 메시지 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 목록 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatMessageDTO.class)))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/rooms/{roomId}/messages")
    ResponseEntity<?> getMessage(
            @Parameter(description = "조회할 채팅방 ID", required = true, example = "1")
            @PathVariable("roomId") int roomId
    );

    @Operation(summary = "내 채팅방 목록 조회", description = "현재 로그인 사용자가 참여 중인 채팅방 목록을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChatDTO.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/rooms/my")
    ResponseEntity<?> getMyChatRooms(@RequestParam(required = false) String before);

    @Operation(summary = "채팅방 생성 또는 기존 방 반환", description = "사용자 목록을 기준으로 새 채팅방을 생성하거나 기존 방을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 생성 or 기존 방 반환",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/rooms")
    ResponseEntity<?> createChatRoom(
            @Parameter(description = "사용자 ID 목록을 포함한 요청 데이터", required = true)
            @RequestBody Map<String, Object> requestData
    );

    @Operation(summary = "게시글 공유", description = "지정된 사용자들에게 게시글을 공유하고 채팅방에 메시지를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공유 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/rooms/share-post")
    ResponseEntity<?> sharePost(
            @Parameter(description = "공유할 게시글 및 사용자 정보", required = true)
            @RequestBody Map<String, Object> requestData
    );

    @Operation(summary = "채팅방 나가기", description = "현재 사용자가 채팅방을 나갑니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "퇴장 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/rooms/leave")
    ResponseEntity<?> leaveChat(
            @Parameter(description = "나갈 채팅방 정보", required = true)
            @RequestBody ChatDTO dto
    );
}
