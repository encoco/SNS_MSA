package com.chatservice.openChat.docs;

import com.chatservice.openChat.dto.OpenChatDTO;
import com.chatservice.openChat.dto.OpenChatMemberDTO;
import com.chatservice.openChat.dto.OpenChatMessageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface OpenDocs {

    @Operation(summary = "전체 오픈채팅방 목록 조회", description = "모든 오픈채팅방의 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OpenChatDTO.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/open")
    ResponseEntity<?> openRoom();

    @Operation(summary = "오픈채팅방 생성", description = "새로운 오픈채팅방을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 완료"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/open")
    ResponseEntity<?> createOpenChat(
            @Parameter(description = "오픈채팅방 생성 정보", required = true)
            @ModelAttribute OpenChatDTO dto
    );

    @Operation(summary = "특정 채팅방 메시지 목록 조회", description = "openChatId에 해당하는 채팅방의 메시지를 모두 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 목록 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OpenChatMessageDTO.class)))),
            @ApiResponse(responseCode = "500", description = "조회 실패", content = @Content)
    })
    @GetMapping("/open/{openChatId}/messages")
    ResponseEntity<List<OpenChatMessageDTO>> getMessages(
            @Parameter(description = "조회할 오픈채팅방 ID", required = true)
            @PathVariable int openChatId
    );

    @Operation(summary = "오픈채팅방 참여", description = "해당 채팅방에 참여합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참여 완료"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/open/{openChatId}/join")
    ResponseEntity<?> joinOpenRoom(
            @Parameter(description = "참여할 오픈채팅방 ID", required = true)
            @RequestBody OpenChatDTO dto
    );

    @Operation(summary = "오픈채팅방 퇴장", description = "해당 채팅방에서 퇴장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "퇴장 완료"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/open/{openChatId}/leave")
    ResponseEntity<Void> leaveOpenChat(
            @Parameter(description = "퇴장할 오픈채팅방 ID", required = true)
            @RequestBody OpenChatMemberDTO dto
    );

    @Operation(summary = "내가 참여 중인 오픈채팅방 목록", description = "현재 로그인 사용자가 참여 중인 채팅방들을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참여 중인 채팅방 목록 반환",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OpenChatMemberDTO.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @GetMapping("/open/my")
    ResponseEntity<?> selectOpenMyRoom();
}
