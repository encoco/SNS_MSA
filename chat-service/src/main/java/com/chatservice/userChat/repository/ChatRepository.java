package com.chatservice.userChat.repository;


import com.chatservice.userChat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatRoomEntity, Integer> {
    void deleteByIdAndUserId(int id, int userId);

    @Query(value = """
      SELECT cr.chat_id
      FROM chat_room cr
      JOIN (
        SELECT cm.chat_id, MAX(cm.date) AS lastDate
        FROM chat_message cm
        JOIN chat_member cmbr ON cmbr.chat_id = cm.chat_id
        WHERE cmbr.user_id = :userId
          AND (:before IS NULL OR cm.date < :before)
        GROUP BY cm.chat_id
        ORDER BY lastDate DESC
        LIMIT :limit
      ) sub ON sub.chat_id = cr.chat_id
      ORDER BY sub.lastDate DESC
      """, nativeQuery = true)
    List<Integer> findPagedRoomIds(
            @Param("userId") int userId,
            @Param("before") String before,
            @Param("limit") int limit
    );
}


