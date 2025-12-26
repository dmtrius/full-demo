package com.example.demo.apps.tasks.backpressure;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;

import static java.lang.IO.println;

@Slf4j
class CustomSubscriber<T> implements Flow.Subscriber<T> {
    private static final int NUM_THREADS = 4;
    private static final int NUM_ITEMS = 2;
    private Flow.Subscription subscription;
    private final ExecutorService executorService
            = Executors.newFixedThreadPool(NUM_THREADS);

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(NUM_ITEMS);
    }

    @Override
    public void onNext(T item) {
        println("Received: " + item);
        executorService.submit(() -> {
            try {
                Thread.sleep(Duration.ofMillis(400)); // Simulate slow processing
                println("Processed:: " + item);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                onError(e);
                Thread.currentThread().interrupt();
            }
            subscription.request(NUM_ITEMS);
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
}
