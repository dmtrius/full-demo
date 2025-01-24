package com.example.demo.apps;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

public class App14 {
    public static void main(String... args) {
        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(
                        URI.create("ws://localhost:8882/event-emitter"),
                        session -> session.send(
                                        Mono.just(session.textMessage("event-spring-reactive-client-websocket")))
                                .thenMany(session.receive()
                                        .map(WebSocketMessage::getPayloadAsText)
                                        .log())
                                .then())
                .block(Duration.ofDays(1L));
    }
}
