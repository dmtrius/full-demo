package com.example.demo.apps;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.time.Duration;

import static java.io.IO.println;

public class App30 {
    static {
        println("START");
    }

    @SneakyThrows
    void main() {
        println("brrr");
        processInfo();
        tryLinker1();
        tryLinker2();
        tryMethodHandler();

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Cleaning up resources...");
        }));
    }

    @SneakyThrows
    void tryMethodHandler() {
        MethodHandle handle = MethodHandles.
                lookup().
                findVirtual(String.class, "toUpperCase",
                        MethodType.methodType(String.class));
        System.out.println(handle.invoke("hello")); // "HELLO"
    }

    void processInfo() {
        ProcessHandle.current().info().command().ifPresent(System.out::println);
    }

    @SneakyThrows
    void tryLinker1() {
        Linker linker = Linker.nativeLinker();

        SymbolLookup stdlib = Linker.nativeLinker().defaultLookup();
        MemorySegment strlenSym = stdlib.find("strlen").orElseThrow();

        MethodHandle strlen = linker.downcallHandle(
                strlenSym,
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
        );

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment cStr = arena.allocateFrom("hello world");

            long length = (long) strlen.invoke(cStr);
            System.out.println("Length: " + length);  // Outputs: Length: 11
        }
    }

    @SneakyThrows
    void tryLinker2() {
        Linker linker = Linker.nativeLinker();
        SymbolLookup stdlib = linker.defaultLookup();

        MemorySegment putsSym = stdlib.find("puts").orElseThrow();

        MethodHandle puts = linker.downcallHandle(
                putsSym,
                FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS)
        );

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment msg = arena.allocateFrom("Hello from native puts!");
            puts.invoke(msg);
        }
    }
}
