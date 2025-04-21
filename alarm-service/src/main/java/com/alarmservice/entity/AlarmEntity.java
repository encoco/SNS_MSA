package com.alarmservice.entity;

import com.alarmservice.dto.AlarmDTO;
import com.common.dto.AlarmEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@Table(name = "alarm")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class AlarmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int alarmId;

    private int recipientId;
    private int senderId;

    private int board_id;
    private String content;

    @Column(name = "isRead")
    private boolean isRead;

    @Builder.Default
    private String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));


    public static AlarmEntity fromEvent(AlarmEvent event) {
        return AlarmEntity.builder()
                .recipientId(event.getReceiverId())
                .senderId(event.getSenderId())
                .board_id(event.getBoardId())
                .content(event.getContent())
                .isRead(false)
                .build();
    }
}
