package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

@Slf4j
public class App7 {
    void main() {
        
    }
    private void t1() {
        synchronized (this) {

        }
    }

    private static void testFlux2() {
        Flux.range(1, 12)
                .map(i -> i + 3)
                .filter(i -> i % 2 == 0)
                .buffer(3)
                .subscribe(System.out::println);
    }

    private static void testFlux() {
        Flux<String> flux = Flux.just("Apple", "Banana", "Cherry", null);
        flux.subscribe(
                item -> System.out.println("Received: " + item),
                error -> System.err.println("Error: [" + error + "]"),
                () -> System.out.println("Completed!")
        );
    }

    private static void testBackpressure() {
        Flux<Integer> flux = Flux.range(1, 42);//.log();
        flux.subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                        request(2);
                    }
                    @Override
                    protected void hookOnNext(Integer item) {
                        System.out.println("Received: " + item);
                        if (item % 2 == 0) {
                            request(1);
                        }
                    }
                }
        );
    }

    private static void testBackpressure2() {
        Flux<String> flux = Flux.just("Apple", "Banana", "Cherry", null)
                .onBackpressureBuffer();
        flux.subscribe(
                item -> System.out.println("Received: " + item),
                error -> System.err.println("Error: [" + error + "]"),
                () -> System.out.println("Completed!")
        );
    }
}
