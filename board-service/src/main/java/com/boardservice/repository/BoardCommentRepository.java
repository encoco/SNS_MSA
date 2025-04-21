package com.boardservice.repository;

import com.boardservice.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BoardCommentRepository extends JpaRepository<CommentEntity, Integer> {
    List<CommentEntity> findByBoardIdOrderByDateDesc(int boardId);
}