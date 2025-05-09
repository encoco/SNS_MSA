package com.chatservice.userChat.repository;


import com.chatservice.userChat.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Integer> {
    List<ChatMessageEntity> findByChatRoom_Id(int chatId);
    @Query(value = """
        SELECT date
        FROM chat_message
        WHERE chat_id = :roomId
        ORDER BY date DESC
        LIMIT 1
        """, nativeQuery = true)
    String findLatestDateByChatRoomId(@Param("roomId") int roomId);

    @Query("""
      SELECT m.chatRoom.id   AS chatId,
             MAX(m.date)     AS lastDate
      FROM ChatMessageEntity m
      WHERE m.chatRoom.id IN :chatIds
      GROUP BY m.chatRoom.id
    """)
    List<ChatIdAndLastDate> findLatestDates(@Param("chatIds") List<Integer> chatIds);

    interface ChatIdAndLastDate {
        Integer getChatId();
        String  getLastDate();   // ChatMessageEntity.date 가 String 이므로 그대로 반환
    }

}
