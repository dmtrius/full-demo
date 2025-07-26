package com.example.demo.apps;

import com.example.demo.apps.tasks.TimeTracker;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static io.vavr.API.println;

public class App26 {
    void main() {
        Mono<String> primaryMono = Mono.empty();
//        Mono<String> fallbackMono = Mono.just("Fallback value");
//        Mono<String> result = primaryMono.switchIfEmpty(fallbackMono);
        Mono<String> result = primaryMono.switchIfEmpty(Mono.defer(this::getFallbackValue));
        println(result.block());
//        UnaryOperator
//        AtomicStampedReference
        TimeTracker.track("Operation >> ", () -> directMemory());
        Function<Integer, Integer> multiplyBy2 = x -> x * 2;
        UnaryOperator<Integer> mBy2 = x -> x * 2;
        println(">>> " +multiplyBy2.equals(mBy2));
        println(multiplyBy2.apply(5));
        println(mBy2.apply(5));
    }

    @SneakyThrows
    private Mono<String> getFallbackValue() {
        TimeUnit.SECONDS.sleep(2);
        return Mono.just("Fallback value");
    }

    @SneakyThrows
    private static void directMemory() {
        int[] heapArray = new int[1024];
        heapArray[0] = 42;
        println("Heap Value: " + heapArray[0]);
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(4096);
        directBuffer.putInt(0, 42);
        int value = directBuffer.getInt(0);
        println("Direct Memory Value: " + value);
    }
}
