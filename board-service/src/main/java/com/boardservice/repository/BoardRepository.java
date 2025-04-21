package com.boardservice.repository;

import com.boardservice.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {
    List<BoardEntity> findByUserIdOrderByDateDesc(int userId);
    BoardEntity findByBoardId(int boardId);
    boolean existsByUserId(int userId);

    @Query(value = "SELECT * FROM board ORDER BY RAND() LIMIT 20", nativeQuery = true)
    List<BoardEntity> findRandomBoards();

    List<BoardEntity> findByUserIdInOrderByDateDesc(List<Integer> followIds);
}
