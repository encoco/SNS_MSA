package com.userservice.kafka;

import com.common.dto.AlarmEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmProducer {

    private final KafkaTemplate<String, AlarmEvent> kafkaTemplate;
    private static final String TOPIC = "alarm-topic";

    public void sendFollowAlarm(int senderId, int recipientId) {
        AlarmEvent event = AlarmEvent.builder()
                .receiverId(recipientId)
                .senderId(senderId)
                .content("당신을 팔로우했습니다.")
                .build();

        kafkaTemplate.send(TOPIC, event);
    }
}