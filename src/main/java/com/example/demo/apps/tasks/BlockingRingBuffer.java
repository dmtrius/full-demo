package com.example.demo.apps.tasks;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingRingBuffer<E> extends AbstractQueue<E> {
    private final Object[] data;
    private final int capacity;
    private static final int WRITE_SEQUENCE = -1;
    private static final int READ_SEQUENCE = 0;
    private int writeIndex = 0;
    private int readIndex = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public BlockingRingBuffer(int capacity) {
        this.capacity = capacity;
        this.data = new Object[capacity];
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
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E poll() {
        lock.lock();
        try {
            while (isEmpty()) {
                notEmpty.await();
            }
            E e = (E) data[readIndex];
            data[readIndex] = null;  // Optional: help GC
            readIndex = (readIndex + 1) % capacity;
            notFull.signal();
            return e;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public E peek() {
        if (isEmpty()) {
            return null;
        }
        return (E) data[READ_SEQUENCE % capacity];
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return (WRITE_SEQUENCE - READ_SEQUENCE) + 1;
    }

    public boolean isEmpty() {
        return WRITE_SEQUENCE < READ_SEQUENCE;
    }

    private boolean isFull() {
        return size() == capacity;
    }

//    @Override
    public int remainingCapacity() {
        return capacity - size();
    }
}