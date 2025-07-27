package com.example.demo.apps.tasks.backpressure;

import static java.util.concurrent.Flow.Publisher;
import static java.util.concurrent.Flow.Subscriber;

import java.io.Closeable;
import java.util.concurrent.SubmissionPublisher;

public class CustomPublisher<T> implements Publisher<T>, Closeable {
    private final SubmissionPublisher<T> submissionPublisher;

    public CustomPublisher() {
        this.submissionPublisher = new SubmissionPublisher<>();
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        submissionPublisher.subscribe(subscriber);
    }

    public void publish(T item) {
        submissionPublisher.submit(item);
    }

    @Override
    public void close() {
        submissionPublisher.close();
    }
}
