package com.alarmservice.kafka;

import com.alarmservice.entity.AlarmEntity;
import com.alarmservice.repository.AlarmRepository;
import com.common.dto.AlarmEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmConsumer {

    private final AlarmRepository repository;

    @KafkaListener(topics = "alarm-topic", groupId = "alarm-service-group")
    public void consume(AlarmEvent event) {
        System.out.println("📥 Kafka 알림 수신: " + event);

        boolean exists = repository.existsBySenderIdAndRecipientIdAndContent(
                event.getSenderId(), event.getReceiverId(), event.getContent());

        if (exists) {
            System.out.println("⚠️ 중복된 알림, 저장하지 않음.");
            return;
        }
        AlarmEntity entity = AlarmEntity.builder()
                .recipientId(event.getReceiverId())
                .senderId(event.getSenderId())
                .board_id(event.getBoardId())
                .content(event.getContent())
                .isRead(false)
                .build();

        repository.save(entity);
    }

}