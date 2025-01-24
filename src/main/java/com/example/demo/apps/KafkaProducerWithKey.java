package com.example.demo.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class KafkaProducerWithKey {

    private static final String TOPIC = "my-super-topic";

    @SneakyThrows
    public static void main(String... args) {
        Properties props = getProperties();
        try (KafkaProducer<String, PrivateUser> producer = new KafkaProducer<>(props)) {
            for (int i = 0; i < 50; ++i) {
                String key = UUID.randomUUID().toString();
                //String value = getRandomString(random.nextInt(10, 20));
                PrivateUser user = getRandomUser();
                String jsonUser = mapper.writeValueAsString(user);
                createAndSendMessage(key, user, producer);
            }
        }
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    private static PrivateUser getRandomUser() {
        Faker faker = new Faker();
//        Status status = Status.values()[random.nextInt(Status.values().length)];
//        Role role = Role.values()[random.nextInt(Role.values().length)];
        return new PrivateUser(
                UUID.randomUUID().toString(),
                faker.name().fullName(),
                faker.internet().emailAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.address().streetAddress(),
                getRandomEnumValue(Status.class),
                getRandomEnumValue(Role.class),
                faker.random().hex()
        );
    }

    private static <T extends Enum<?>> T getRandomEnumValue(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Enum has no values");
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(values.length);
        return values[randomIndex];
    }

    private static final Random random = new Random();
    private static String getRandomString(int length) {
        String characters = " ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        return random.ints(length, 0, characters.length())
                .mapToObj(characters::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    private static void createAndSendMessage(String key, PrivateUser value,
                                             KafkaProducer<String, PrivateUser> producer) {
        ProducerRecord<String, PrivateUser> kafkaRecord
                = new ProducerRecord<>(TOPIC, null, key, value,
                getHeaders(value));
        sendMessage(producer, kafkaRecord);
        producer.flush();
    }

    private static List<Header> getHeaders(PrivateUser value) {
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("trace-id",
                String.valueOf(value.hashCode() ).getBytes()));
        headers.add(new RecordHeader("message-type", "JSON".getBytes()));
        return headers;
    }

    private static void sendMessage(KafkaProducer<String, PrivateUser> producer,
                                    ProducerRecord<String, PrivateUser> kafkaRecord) {
        producer.send(kafkaRecord, (metadata, exception) -> {
            if (exception == null) {
                System.out.println("Message sent successfully. Offset: " + metadata.offset() + ", Partition: " + metadata.partition());
            } else {
                System.err.println("Error sending message: " + exception.getMessage());
            }
        });
    }

    private static Properties getProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Replace it with your Kafka brokers
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Serialize keys as strings
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // Serialize values as strings
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class.getName()); // Serialize values as strings

        // Optional but recommended for performance
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // Ensure all in-sync replicas acknowledge the "write"
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // Exactly-once semantics (requires max.in.flight.requests.per.connection=5)
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5"); // required for idempotence
        return props;
    }
}

enum Status {
    ACTIVE, INACTIVE, DELETED
}

enum Role {
    ADMIN, USER, GUEST, MANAGER, CUSTOMER
}

record PrivateUser(
        String id,
        String name,
        String email,
        String phoneNumber,
        String address,
        Status status,
        Role role,
        String token
){}
