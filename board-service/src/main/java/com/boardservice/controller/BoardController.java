package com.boardservice.controller;

import com.boardservice.dto.BoardDTO;
import com.boardservice.dto.BoardLikeDTO;
import com.boardservice.dto.CommentDTO;
import com.boardservice.service.BoardService;
import com.common.security.AuthInfoUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/mainboardList")
    public ResponseEntity<?> mainboardList() {
        try {
            int userId = AuthInfoUtil.getUserId();

            List<BoardDTO> posts = boardService.getFollowPost(userId);
            if (posts == null || posts.isEmpty()) {
                posts = boardService.getRandomPost();
            }

            List<BoardLikeDTO> like = boardService.getLike(posts);

            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("likes", like);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("mainboardList error : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러: /mainboardList");
        }
    }

    @GetMapping("/userPosts")
    public ResponseEntity<?> userPosts(@RequestParam("userId") int userId) {
        try {
            int myId = AuthInfoUtil.getUserId();
            Map<String, Object> response = boardService.getUserPosts(userId, myId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("userPosts error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("/userPosts");
        }
    }

    @PostMapping("/boardWrite")
    public ResponseEntity<?> writeBoard(@RequestParam("content") String content,
                                        @RequestParam(value = "img", required = false) List<MultipartFile> imgs) {
        try {
            String nickname = AuthInfoUtil.getNickname();
            int userId = AuthInfoUtil.getUserId();

            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setId(userId);
            boardDTO.setNickname(nickname);
            boardDTO.setContent(content);
            System.out.println(boardDTO);
            if (imgs != null && !imgs.isEmpty()) {
                boardDTO.setImg(imgs); // 여러 이미지 파일 설정
            }

            boardService.writeBoard(boardDTO);
            return ResponseEntity.ok("글이 성공적으로 작성되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글 작성 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/boardLike")
    public ResponseEntity<?> boardLike(@RequestParam(value = "boardId") int boardId,
                                       @RequestParam(value = "writerId") int writerId) {
        try {
            int userId = AuthInfoUtil.getUserId();
            BoardLikeDTO dto = new BoardLikeDTO();
            dto.setBoard_id(boardId);
            dto.setId(userId);
            int result = boardService.boardLike(dto, writerId);

            if (result == 0) {
                return ResponseEntity.ok("fail");
            } else if (result == 1) {
                return ResponseEntity.ok("success");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected result");
            }
        } catch (Exception e) {
            System.out.println("boardLike error : " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("boardLIke");
        }
    }

    @GetMapping("/getComments")
    public ResponseEntity<?> getComments(@RequestParam(value = "boardId") int boardId) {
        try {
            List<CommentDTO> comments = boardService.getComments(boardId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            System.out.println("댓글 에러 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 불러오기 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/CommentWrite")
    public ResponseEntity<?> writeComment(@RequestBody CommentDTO dto) {
        try {
            dto.setId(AuthInfoUtil.getUserId());
            dto.setNickname(AuthInfoUtil.getNickname());;
            boardService.writeComment(dto);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 작성 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/boardUpdate")
    public ResponseEntity<?> updateBoard(@RequestParam("content") String content,
                                         @RequestParam(value = "img", required = false) List<MultipartFile> imgs,
                                         @RequestParam(value = "imgpath", required = false) String imgpath,
                                         @RequestParam("board_id") int board_id) {
        try {
            BoardDTO boardDTO = new BoardDTO();
            boardDTO.setBoard_id(board_id);
            boardDTO.setContent(content);

            if (imgpath != null && !imgpath.isEmpty()) boardDTO.setImgpath(imgpath);
            else if (imgs != null && !imgs.isEmpty())  boardDTO.setImgpath("");
            else                                       boardDTO.setImgpath(null);

            if (imgs != null && !imgs.isEmpty()) {
                boardDTO.setImg(imgs);
            }

            boardService.updatePost(boardDTO);
            return ResponseEntity.ok("수정 완료");
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글 update 실패.");
        }
    }


    @PostMapping("/EditComment")
    public ResponseEntity<?> updateComment(@RequestBody CommentDTO commentDTO) {
        try {
            boardService.updateComment(commentDTO);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 update.");
        }
    }

    @PostMapping("/DeleteComment")
    public ResponseEntity<?> deleteComment(@RequestBody CommentDTO commentDTO) {
        try {
            boardService.DeleteComment(commentDTO);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 update.");
        }
    }

    @PostMapping("/boardDelete")
    public ResponseEntity<?> deleteBoard(@RequestBody int board_id) {
        try {
            boardService.DeleteBoard(board_id);
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("글 삭제 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/findPost")
    public ResponseEntity<?> findPost(@RequestParam(value = "boardId") int boardId) {
        try {
            int userId = AuthInfoUtil.getUserId();

            BoardDTO post = boardService.getSharePost(boardId);
            List<BoardLikeDTO> like = boardService.getShareLike(boardId);

            Map<String, Object> response = new HashMap<>();
            response.put("posts", post);
            response.put("likes", like);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 불러오기 중 오류가 발생했습니다.");
        }
    }
}