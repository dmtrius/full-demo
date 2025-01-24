package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PubSubTest {
    private final Publisher pub = new Publisher();
    private final Subscriber sub1 = mock(Subscriber.class);
    private final Subscriber sub2 = mock(Subscriber.class);

    @BeforeEach
    void setUp() {
        pub.addSubscriber(sub1);
        pub.addSubscriber(sub2);
    }

    @Test
    void publisherSendsMessageToAllSubscribers() {
        pub.send("Hello");
        verify(sub1).receive("Hello");
        verify(sub2).receive("Hello");
    }

    @Test
    void testSendInOrder() {
        pub.send("Hello");
        InOrder inorder = inOrder(sub1, sub2);
        inorder.verify(sub1).receive("Hello");
        inorder.verify(sub2).receive("Hello");
    }
}

@Slf4j
final class Publisher {
    private final List<Subscriber> subscribers = new ArrayList<Subscriber>();

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void send(String message) {
        for (Subscriber subscriber : subscribers) {
            try {
                subscriber.receive(message);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}

interface Subscriber {
    void receive(String message);
}
