package com.boardservice.service;


import com.boardservice.client.UserClient;
import com.boardservice.dto.*;
import com.boardservice.entity.BoardEntity;
import com.boardservice.entity.BoardLikeEntity;
import com.boardservice.entity.CommentEntity;
import com.boardservice.kafka.AlarmProducer;
import com.boardservice.repository.BoardCommentRepository;
import com.boardservice.repository.BoardLikeRepository;
import com.boardservice.repository.BoardRepository;
import com.common.dto.AlarmEvent;
import com.common.dto.UsersInfoDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    public final BoardRepository boardRepository;
    public final BoardCommentRepository boardCommentRepository;
    public final BoardLikeRepository boardlike;
    private final UserClient userClient;
    private final AlarmProducer alarmProducer;
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;

    public String uploadFile(MultipartFile file, String path) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension; // 고유번호 + 확장자로 이름 리네임

//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(file.getSize()); // 파일 크기 설정
//
//        // S3에 파일 업로드
//        try {
//            s3Config.amazonS3Client()
//                    .putObject(new PutObjectRequest(bucket + "/" + path, newFilename, file.getInputStream(), metadata));
//        } catch (SdkClientException | java.io.IOException e) {
//            e.printStackTrace();
//        }

        // 올바른 경로의 URL 주소를 생성하여 저장
//        String s3Url = s3Config.amazonS3Client().getUrl(bucket + "/" + path, newFilename).toString();
        return "";
    }

    public List<BoardLikeDTO> getLike(int userId) {
        List<BoardEntity> userPosts = boardRepository.findByUserIdOrderByDateDesc(userId);
        if (!userPosts.isEmpty()) {
            List<BoardLikeDTO> dtos = new ArrayList<>();
            for (BoardEntity post : userPosts) {
                List<BoardLikeEntity> likesForPost = boardlike.findByboardId(post.getBoardId());
                dtos.addAll(BoardLikeDTO.ToDtoList(likesForPost));
            }

            return dtos;
        }
        return null;
    }

    public void writeBoard(BoardDTO boardDTO) {
        String imgPath = "";

//        if (boardDTO.getImg() != null) {
//            for (MultipartFile img : boardDTO.getImg()) {
//                if (imgPath.isEmpty() || imgPath == "") {
//                    imgPath += uploadFile(img, "image");
//                } else {
//                    imgPath += "|" + uploadFile(img, "image");
//                }
//            }
//            boardDTO.setImgpath(imgPath);
//        }
        BoardEntity board = BoardEntity.toEntity(boardDTO);
        boardRepository.save(board);
    }

    @Transactional
    public void writeComment(CommentDTO commentDTO) {
        CommentEntity commentEntity = CommentEntity.toEntity(commentDTO);
        BoardEntity entity = boardRepository.findByBoardId(commentDTO.getBoard_id());
        int writerId = entity.getUserId();
        if (commentDTO.getId() != writerId) {
            AlarmEvent event = AlarmEvent.builder()
                    .senderId(commentDTO.getId())
                    .receiverId(writerId)
                    .boardId(commentDTO.getBoard_id())
                    .content("님이 댓글을 남겼습니다.")
                    .build();
            alarmProducer.send(event);
        }
        boardCommentRepository.save(commentEntity);
    }

    @Transactional
    public int boardLike(BoardLikeDTO dto, int writerId) {
        BoardLikeEntity entity = BoardLikeEntity.toEntity(dto);
        Optional<BoardLikeEntity> existingLike = boardlike.findByBoardIdAndUserId(dto.getBoard_id(), dto.getId());

        if (existingLike.isPresent()) {
            // 이미 "좋아요"가 존재한다면 삭제
            boardlike.delete(existingLike.get());
            return 0;
        } else {
            if (dto.getId() != writerId) {
                System.out.println("글 좋아요 전송");
                AlarmEvent event = AlarmEvent.builder()
                        .senderId(dto.getId())
                        .receiverId(writerId)
                        .boardId(dto.getBoard_id())
                        .content("님이 글을 좋아합니다.")
                        .build();
                alarmProducer.send(event);
            }
            boardlike.save(entity);
            return 1;
        }
    }

    @Transactional
    public void updatePost(BoardDTO dto) { // findById == select * from board where board_id = dto.getBoard_id();
        BoardEntity post = boardRepository.findById(dto.getBoard_id()).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        String imgpath = dto.getImgpath();
//        if (dto.getImg() != null) {
//            for (MultipartFile img : dto.getImg()) {
//                if (imgpath.isEmpty() || imgpath == "") {
//                    imgpath += uploadFile(img, "image");
//                } else {
//                    imgpath += "|" + uploadFile(img, "image");
//                }
//            }
//        }
        post.setUpdateContent(dto.getContent(), imgpath);
    }

    @Transactional
    public void updateComment(CommentDTO commentDTO) {
        CommentEntity board = CommentEntity.toEntity(commentDTO);
        boardCommentRepository.save(board);
    }

    @Transactional
    public void DeleteBoard(int board_id) {
        boardRepository.deleteById(board_id);
    }

    public List<BoardDTO> getFollowPost(int userId) {
        List<Integer> followIds = userClient.getFollowingIds(userId);
        if (followIds.isEmpty()) return List.of();

        List<BoardEntity> entityList = boardRepository.findByUserIdInOrderByDateDesc(followIds);

        List<Integer> authorIds = entityList.stream()
                .map(BoardEntity::getUserId)
                .distinct()
                .toList();

        List<UsersInfoDTO> userInfos = userClient.getUsersInfo(authorIds);

        return BoardDTO.toDtoList(entityList, userInfos);
    }


    public List<BoardLikeDTO> getLike(List<BoardDTO> posts) {
        List<Integer> postIds = posts.stream().map(BoardDTO::getBoard_id).collect(Collectors.toList());
        List<BoardLikeEntity> likeEntities = boardlike.findByBoardIds(postIds);

        return BoardLikeDTO.ToDtoList(likeEntities);
    }

    // 댓글 조회
    public List<CommentDTO> getComments(int boardId) {
        List<CommentEntity> commentEntities = boardCommentRepository.findByBoardIdOrderByDateDesc(boardId);
        return CommentEntity.ToDtoList(commentEntities, userClient);
    }

    @Transactional
    public void DeleteComment(CommentDTO commentDTO) {
        CommentEntity board = CommentEntity.toEntity(commentDTO);
        boardCommentRepository.delete(board);
    }

    public BoardDTO getSharePost(int board_id) {
        BoardEntity entity = boardRepository.findByBoardId(board_id);
        if (entity == null) return null;

        // 유저 정보 조회
        UsersInfoDTO userInfo = userClient.getUserInfo(entity.getUserId());

        return BoardDTO.toDTO(entity, userInfo);
    }

    public List<BoardLikeDTO> getShareLike(int boardId) {
        List<BoardLikeEntity> likesForPost = boardlike.findByboardId(boardId);

        return BoardLikeDTO.ToDtoList(likesForPost);

    }

    public List<BoardDTO> getRandomPost() {
        try {
            List<BoardEntity> rand = boardRepository.findRandomBoards();
            List<Integer> userIds = rand.stream().map(BoardEntity::getUserId).distinct().toList();

            List<UsersInfoDTO> userInfos = userIds.stream().map(userClient::getUserInfo).toList();
            return BoardDTO.toDtoList(rand,userInfos);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public Map<String, Object> getUserPosts(int userId, int myId) {
        List<BoardEntity> entities = boardRepository.findByUserIdOrderByDateDesc(userId);

        List<Integer> userIds = entities.stream()
                .map(BoardEntity::getUserId)
                .distinct()
                .toList();

        List<UsersInfoDTO> userInfos = userClient.getUsersInfo(userIds);

        List<BoardDTO> posts = BoardDTO.toDtoList(entities, userInfos);
        List<BoardLikeDTO> likes = getLike(userId);
        boolean follow = userClient.checkFollow(myId, userId);

        UsersInfoDTO userInfo = userInfos.stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("likes", likes);
        response.put("userInfo", userInfo);
        response.put("follow", follow);

        return response;
    }
}