package com.example.demo.apps.tasks;

import static java.lang.IO.println;

public class CircularBufferExample {
    @SuppressWarnings("all")
    void main() {
        BlockingRingBuffer<String> buffer
                = new BlockingRingBuffer<>(5, String[]::new);
        buffer.offer("A");  // true
        buffer.offer("B");  // true
        println("poll: " + buffer.poll());  // "A"
        println("size: " + buffer.size());  // 1
        println("remaining capacity :" + buffer.remainingCapacity());  // 1
        buffer.offer("C");
        buffer.offer("D");
        buffer.offer("E");
        buffer.offer("F");
        buffer.offer("G");
        buffer.offer("H");
        buffer.offer("I");
        println("size: " + buffer.size());
        println("remaining capacity :" + buffer.remainingCapacity());
        println("poll: " + buffer.poll());
        println("size: " + buffer.size());
        println("remaining capacity :" + buffer.remainingCapacity());
    }
}
