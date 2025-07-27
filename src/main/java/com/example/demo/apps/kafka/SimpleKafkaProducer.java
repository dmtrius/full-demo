package com.example.demo.apps.kafka;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class SimpleKafkaProducer {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(KafkaConfig.getProducerConfig());
             Scanner scanner = new Scanner(System.in)) {
//            println("Enter messages to send to Kafka (type 'exit' to quit):");
//            while (true) {
//                String line = scanner.nextLine();
//                if ("exit".equalsIgnoreCase(line)) {
//                    break;
//                }
//                send(producer, line);
//            }
            sendRandomMessages(producer, 20);
        }
    }

    private static void send(KafkaProducer<String, String> producer, String line) {
        ProducerRecord<String, String> record = new ProducerRecord<>(KafkaConfig.TOPIC, line);
        producer.send(record, (metadata, exception) -> {
            if (Objects.isNull(exception)) {
                log.info("Sent message to topic {} partition {} offset {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            } else {
                log.error(exception.getMessage());
            }
        });
    }

    private static void sendRandomMessages(KafkaProducer<String, String> producer, int numMessages) {
        for (int i = 0; i < numMessages; ++i) {
            String message = Faker.instance().animal().name();
            send(producer, message);
        }
    }
}
