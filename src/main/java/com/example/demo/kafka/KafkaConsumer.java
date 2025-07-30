package com.example.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {
    @Value("${spring.kafka.consumer.topic}")
    private String topic;
    @KafkaListener(topics = "${spring.kafka.consumer.topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload String message) {
        log.info("Received message from topic {}: {}", topic, message);
    }

    @KafkaListener(topics = "WTF",
            groupId = "${spring.kafka.consumer.group-id}")
    public void onWTFMessage(@Payload String message) {
        log.info("Handle new messages -> #{}# to Topic [WTF]", message);
    }
}
