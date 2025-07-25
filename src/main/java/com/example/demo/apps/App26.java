package com.example.demo.apps;

import reactor.core.publisher.Mono;

public class App26 {
    void main() {
        Mono<String> primaryMono = Mono.empty();
        Mono<String> fallbackMono = Mono.just("Fallback value");
        Mono<String> result = primaryMono.switchIfEmpty(fallbackMono);
//        Mono<String> result = primaryMono.switchIfEmpty(Mono.defer(() -> Mono.just("Deferred fallback value")));

        System.out.println(result.block());
    }
}
