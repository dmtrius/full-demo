package com.example.demo.apps.tasks.backpressure;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.util.concurrent.*;
import java.util.concurrent.Flow.*;
import java.util.stream.IntStream;

import static java.lang.IO.println;

@Slf4j
public class BackpressureExample {

    private static final int NUM_THREADS = 4;

    void main() throws InterruptedException {
        // Create a custom publisher
        try (CustomPublisher<Integer> publisher = new CustomPublisher<>()) {
            // Create a subscriber and register it with the publisher
            var subscriber = new Subscriber<Integer>() {
                private Subscription subscription;
                private final ExecutorService executorService
                        = Executors.newFixedThreadPool(NUM_THREADS);

                @Override
                public void onSubscribe(Subscription subscription) {
                    this.subscription = subscription;
                    this.subscription.request(2);
                }

                @Override
                public void onNext(Integer item) {
                    println("Received: " + item);
                    executorService.submit(() -> {
                        try {
                            Thread.sleep(1000); // Simulate slow processing
                            println("Processed:: " + item);
                        } catch (InterruptedException e) {
                            log.error(e.getMessage());
                            onError(e);
                            Thread.currentThread().interrupt();
                        }
                        subscription.request(2);
                    });
                }

                @Override
                public void onError(Throwable throwable) {
                    log.error("Error: {}", throwable.getMessage());
                    subscription.cancel();
                    executorService.shutdown();
                }

                @Override
                public void onComplete() {
                    println("Completed");
                    executorService.shutdown();
                }
            };

            publisher.subscribe(subscriber);

            // Publish items
            SecureRandom random = new SecureRandom();
            IntStream.generate(() -> random.nextInt(1, 42))
                    .limit(20)
                    .forEach(publisher::publish);

            // Wait for subscriber to finish processing and close the publisher
            Thread.sleep(5000);
        }
    }
}
