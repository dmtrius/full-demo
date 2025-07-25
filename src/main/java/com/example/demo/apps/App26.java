package com.example.demo.apps;

import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

public class App26 {
    void main() {
        Mono<String> primaryMono = Mono.empty();
//        Mono<String> fallbackMono = Mono.just("Fallback value");
//        Mono<String> result = primaryMono.switchIfEmpty(fallbackMono);
        Mono<String> result = primaryMono.switchIfEmpty(Mono.defer(this::getFallbackValue));

        System.out.println(result.block());
    }

    private Mono<String> getFallbackValue() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ignore) {}
        return Mono.just("Fallback value");
    }
}
