package com.example.demo.apps.tasks.backpressure;

import java.security.SecureRandom;
import java.util.stream.IntStream;

public class BackpressureExample {

    private static final SecureRandom random = new SecureRandom();

    void main() {
        try (final CustomPublisher<Integer> publisher = new CustomPublisher<>()) {
            var subscriber = new CustomSubscriber<Integer>();
            publisher.subscribe(subscriber);
            IntStream.generate(() -> random.nextInt(1, 43))
                    .limit(20)
                    .forEach(publisher::publish);
            Thread.sleep(5000);
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
        }
    }
}
