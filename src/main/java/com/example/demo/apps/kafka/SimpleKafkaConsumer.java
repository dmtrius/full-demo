package com.example.demo.apps.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.time.Duration;
import java.util.Collections;

public class SimpleKafkaConsumer {
    void main() {
        String topic = WordCountExample.OUTPUT_TOPIC;
        String groupId = KafkaConfig.GROUP;

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(KafkaConfig.getConsumerConfig(groupId))) {
            consumer.subscribe(Collections.singletonList(topic));
            IO.println("Listening for messages on topic: " + topic);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, String> rrd : records) {
                    System.out.printf("Received: key=%s, value=%s, offset=%d, partition=%d%n",
                            rrd.key(), rrd.value(), rrd.offset(), rrd.partition());
                }
            }
        }
    }
}
