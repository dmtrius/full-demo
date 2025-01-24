package com.example.demo.beans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
@Profile("!test")
public class ReactiveWebSocketConfiguration {

    private final ReactiveWebSocketHandler reactiveWebSocketHandler;

    public ReactiveWebSocketConfiguration(
            @Qualifier("reactiveWebSocketHandler")
            ReactiveWebSocketHandler reactiveWebSocketHandler) {
        this.reactiveWebSocketHandler = reactiveWebSocketHandler;
    }

    @Bean("reactiveWebSocketHandlerMapping")
    public HandlerMapping reactiveWebSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/reactive-event-emitter", reactiveWebSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter(webSocketService());
    }

    @Bean
    public WebSocketService webSocketService() {
        TomcatRequestUpgradeStrategy tomcatRequestUpgradeStrategy = new TomcatRequestUpgradeStrategy();
        tomcatRequestUpgradeStrategy.setMaxSessionIdleTimeout(10000L);
        tomcatRequestUpgradeStrategy.setAsyncSendTimeout(10000L);
        return new HandshakeWebSocketService(tomcatRequestUpgradeStrategy);
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();

        serverEndpointExporter.setAnnotatedEndpointClasses(WebSocketController.class);

        return serverEndpointExporter;
    }
}
