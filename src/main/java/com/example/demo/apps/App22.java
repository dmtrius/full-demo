package com.example.demo.apps;

import java.util.ArrayList;
import java.util.List;

public class App22 {
    void main() {
        List<Subscriber> subscribers = new ArrayList<>();
        // init subscribers
        subscribers.add(new Subscriber(1, "name_1", NotificationType.EMAIL));
        subscribers.add(new Subscriber(2, "name_2", NotificationType.PUSH));
        subscribers.add(new Subscriber(3, "name_4", NotificationType.SMS));

        subscribers.forEach(s -> sendNotification(s));
    }

    private static Message getMessage(Subscriber subscriber) {
        return new Message(1, "Hello, " + subscriber.name());
    }

    private static void sendNotification(Subscriber subscriber) {
        // implement subs bu type
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
        subscribers.forEach(s -> s.send(getMessage(s.getSubscriber())));
    }
}

interface Subscription {
    void send(Message message);
    Subscriber getSubscriber();
}

class SMS implements Subscription {
    private final Subscriber subscriber;
    SMS(Subscriber subscriber) {
        this.subscriber = subscriber;
    }
    @Override
    public void send(Message message) {
        System.out.println("SMS: " + message);
    }

    @Override
    public Subscriber getSubscriber() {
        return subscriber;
    }
}
class Email implements Subscription {
    private final Subscriber subscriber;
    Email(Subscriber subscriber) {
        this.subscriber = subscriber;
    }
    @Override
    public void send(Message message) {
        System.out.println("EMAIL: " + message);
    }

    @Override
    public Subscriber getSubscriber() {
        return subscriber;
    }
};
class Push implements Subscription {
    private final Subscriber subscriber;
    Push(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void send(Message message) {
        System.out.println("PUSH: " + message);
    }

    @Override
    public Subscriber getSubscriber() {
        return subscriber;
    }
};

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
