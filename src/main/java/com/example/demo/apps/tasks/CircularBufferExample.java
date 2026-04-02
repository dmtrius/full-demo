package com.example.demo.apps.tasks;

import static java.lang.IO.println;

public class CircularBufferExample {
    void main() {
        BlockingRingBuffer<String> buffer = new BlockingRingBuffer<>(5);
        buffer.offer("A");  // true
        buffer.offer("B");  // true
        println(buffer.poll());  // "A"
        println(buffer.size());  // 1
    }
}
