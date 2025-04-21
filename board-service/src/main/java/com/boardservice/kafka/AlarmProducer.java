package com.boardservice.kafka;

import com.common.dto.AlarmEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_NAME = "alarm-topic";

    public void send(AlarmEvent event) {
        System.out.println("send 내부 event : " + event);
        kafkaTemplate.send(TOPIC_NAME, event);
        System.out.println("🔥 Kafka 알림 전송 완료: " + event);
    }
}