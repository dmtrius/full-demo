package com.example.demo.apps.algo;

import java.util.function.Supplier;

import static io.vavr.API.println;

public class TCO {

    void main() {
        int result = factorialTail(5, 1).invoke();
        println(result);
    }

    private TailCall<Integer> factorialTail(int n, int acc) {
        if (n == 0) return TailCall.done(acc);
        return TailCall.call(() -> factorialTail(n - 1, acc * n));
    }
}

interface TailCall<T> {
    TailCall<T> next();
    boolean isComplete();
    T result();

    default T invoke() {
        TailCall<T> step = this;
        while (!step.isComplete()) {
            step = step.next();
        }
        return step.result();
    }

    static <T> TailCall<T> done(T value) {
        return new Done<>(value);
    }

    static <T> TailCall<T> call(Supplier<TailCall<T>> next) {
        return new Call<>(next);
    }
}

class Done<T> implements TailCall<T> {
    private final T value;
    Done(T value) { this.value = value; }
    public TailCall<T> next() { throw new UnsupportedOperationException(); }
    public boolean isComplete() { return true; }
    public T result() { return value; }
}

class Call<T> implements TailCall<T> {
    private final Supplier<TailCall<T>> next;
    Call(Supplier<TailCall<T>> next) { this.next = next; }
    public TailCall<T> next() { return next.get(); }
    public boolean isComplete() { return false; }
    public T result() { throw new UnsupportedOperationException(); }
}
