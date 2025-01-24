package com.example.demo.service;

import com.example.demo.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    @Value("${spring.kafka.consumer.topic}")
    private String topic;

    private final KafkaProducer kafkaProducer;

    public KafkaService(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public String sendMessage(String message) {
        return sendMessage(this.topic, message);
    }

    public String sendMessage(String topic, String message) {
        kafkaProducer.sendMessage(topic, message);
        return String.format("Message sent: [%s] to Topic [%s]", message, topic);
    }
}
