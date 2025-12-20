package com.example.demo.apps;

import java.util.ArrayList;
import java.util.List;

import static java.lang.IO.println;

public class App22 {
    void main() {
        List<Subscriber> subscribers = new ArrayList<>();
        // init subscribers
        subscribers.add(new Subscriber(1, "name_1", NotificationType.EMAIL));
        subscribers.add(new Subscriber(2, "name_2", NotificationType.PUSH));
        subscribers.add(new Subscriber(3, "name_4", NotificationType.SMS));

        subscribers.forEach(this::sendNotification);
    }

    private Message getMessage(Subscriber subscriber) {
        return new Message(1, "Hello, " + subscriber.name());
    }

    private void sendNotification(Subscriber subscriber) {
        // implement subs by type
        List<Subscription> subscribers = new ArrayList<>();
        switch (subscriber.type()) {
            case SMS:
                subscribers.add(new SMS(subscriber));
                break;
            case PUSH:
                subscribers.add(new Push(subscriber));
                break;
            case EMAIL:
                subscribers.add(new Email(subscriber));
                break;
            default:
                throw new IllegalArgumentException("Unsupported notification type");
        }
        subscribers.forEach(s -> s.send(getMessage(s.subscriber())));
    }
}

interface Subscription {
    void send(Message message);
    Subscriber subscriber();
}

record SMS(Subscriber subscriber) implements Subscription {
    @Override
    public void send(Message message) {
        println("SMS: " + message);
    }
}

record Email(Subscriber subscriber) implements Subscription {
    @Override
    public void send(Message message) {
        println("EMAIL: " + message);
    }
}

record Push(Subscriber subscriber) implements Subscription {

    @Override
    public void send(Message message) {
        println("PUSH: " + message);
    }
}

record Subscriber (
    int id,
    String name,
    NotificationType type
) {}

record Message (
    int id,
    String message) {
}

enum NotificationType {
    SMS, PUSH, EMAIL
}
