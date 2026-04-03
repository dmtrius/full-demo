package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.IntStream;

import static java.lang.IO.println;

@Slf4j
public class FlowExample {
    void main() throws InterruptedException {
        // Create a publisher
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        // Create a subscriber
        Flow.Subscriber<String> subscriber = new Flow.Subscriber<>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1); // Request the first item
            }

            @Override
            public void onNext(String item) {
                println("Received: " + item);
                subscription.request(1); // Request the next item
            }

            @Override
            public void onError(Throwable t) {
                log.error(t.getMessage());
            }

            @Override
            public void onComplete() {
                println("Done");
            }
        };

        // Subscribe the subscriber to the publisher
        publisher.subscribe(subscriber);

        // Publish some items
        publisher.submit("Hello");
        publisher.submit("World");

        IntStream.range(1, 11).forEach(i -> publisher.submit("m: " + i));

        // Close the publisher
        publisher.close();

        // Allow some time for the subscriber to process the items
        Thread.sleep(1000);
    }
}