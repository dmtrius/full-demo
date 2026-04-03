package com.example.demo.apps.tasks;

import org.jspecify.annotations.NonNull;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntFunction;

public class BlockingRingBuffer<E> extends AbstractQueue<E> implements EnrichedQueue<E> {
    private final E[] data;
    private final int capacity;
    private int writeIndex = 0;
    private int readIndex = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public BlockingRingBuffer(int capacity, IntFunction<E[]> arrayFactory) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.capacity = capacity;
        this.data = arrayFactory.apply(capacity);
    }

    @Override
    public boolean offer(E e) {
        lock.lock();
        try {
            while (isFull()) {
                notFull.await();
            }
            data[writeIndex] = e;
            writeIndex = (writeIndex + 1) % capacity;
            notEmpty.signal();
            return true;
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E poll() {
        lock.lock();
        try {
            while (isEmpty()) {
                notEmpty.await();
            }
            E e = data[readIndex];
            data[readIndex] = null;  // Optional: help GC
            readIndex = (readIndex + 1) % capacity;
            notFull.signal();
            return e;
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E peek() {
        lock.lock();
        try {
            if (isEmpty()) {
                return null;
            }
            return data[readIndex];
        } finally {
            lock.unlock();
        }
    }

    @Override
    public @NonNull Iterator<E> iterator() {
        final Object[] snapshot;
        final int size = size();
        lock.lock();
        try {
            snapshot = new Object[size];
            for (int i = 0; i < size; i++) {
                int idx = (readIndex + i) % capacity;
                snapshot[i] = data[idx];
            }
        } finally {
            lock.unlock();
        }

        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < snapshot.length;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (cursor >= snapshot.length) {
                    throw new NoSuchElementException();
                }
                return (E) snapshot[cursor++];
            }
        };
    }

    @Override
    public int size() {
        // Compute size based on readIndex and writeIndex
        int w = writeIndex;
        int r = readIndex;
        if (w >= r) {
            return w - r;
        }
        return capacity - r + w;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    private boolean isFull() {
        return size() == capacity;
    }

    @Override
    public int remainingCapacity() {
        return capacity - size();
    }
}

interface EnrichedQueue<E> extends Queue<E> {
    int remainingCapacity();
}
