package com.example.demo.apps.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.Collections;

public class SimpleKafkaConsumer {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        String topic = KafkaConfig.TOPIC;
        String groupId = KafkaConfig.GROUP;

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaConfig.getConsumerConfig(groupId))) {
            consumer.subscribe(Collections.singletonList(topic));
            System.out.println("Listening for messages on topic: " + topic);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Received: key=%s, value=%s, offset=%d, partition=%d%n",
                            record.key(), record.value(), record.offset(), record.partition());
                }
            }
        }
    }
}
