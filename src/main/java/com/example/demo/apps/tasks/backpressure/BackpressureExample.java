package com.example.demo.apps.tasks.backpressure;

import lombok.SneakyThrows;

import java.security.SecureRandom;
import java.util.stream.IntStream;

public class BackpressureExample {
    @SneakyThrows
    void main() {
        try (final CustomPublisher<Integer> publisher = new CustomPublisher<>()) {
            var subscriber = new CustomSubscriber<Integer>();
            publisher.subscribe(subscriber);

            SecureRandom random = new SecureRandom();
            IntStream.generate(() -> random.nextInt(1, 43))
                    .limit(20)
                    .forEach(publisher::publish);

            Thread.sleep(5000);
        }
    }
}
