package com.example.demo.apps.tasks;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Either<L, R> {

    // Left subclass representing the left value
    public static final class Left<L, R> extends Either<L, R> {
        public Left(L left) {
            super(left, null);
        }
    }

    // Right subclass representing the right value
    public static final class Right<L, R> extends Either<L, R> {
        public Right(R right) {
            super(null, right);
        }
    }

    private final L left;
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    // Factory methods to create Left or Right instances
    public static <L, R> Either<L, R> ofLeft(L left) {
        return new Left<>(left);
    }

    public static <L, R> Either<L, R> ofRight(R right) {
        return new Right<>(right);
    }

    // Predicate-based factory methods
    public static <L, R> Either<L, R> predicate(Supplier<Boolean> predicate, Supplier<L> left, Supplier<R> right) {
        return !predicate.get() ? ofLeft(left.get()) : ofRight(right.get());
    }

    public static <L, R> Either<L, R> predicate(boolean predicate, L left, R right) {
        return predicate(() -> predicate, () -> left, () -> right);
    }

    // Optional getters for left and right
    public Optional<L> left() {
        return Optional.ofNullable(left);
    }

    public Optional<R> right() {
        return Optional.ofNullable(right);
    }

    // Check which side is present
    public boolean isLeft() {
        return left().isPresent();
    }

    public boolean isRight() {
        return right().isPresent();
    }

    // Map and flatMap operate on the right value (right-biased)
    public <R2> Either<L, R2> flatMap(Function<R, Either<L, R2>> f) {
        if (isLeft()) {
            return Either.ofLeft(left);
        }
        return f.apply(right);
    }

    public <R2> Either<L, R2> map(Function<R, R2> f) {
        return flatMap(r -> Either.ofRight(f.apply(r)));
    }

    // Consumers for left and right values
    public void ifLeft(Consumer<L> f) {
        if (isLeft()) {
            f.accept(left);
        }
    }

    public void ifRight(Consumer<R> f) {
        if (isRight()) {
            f.accept(right);
        }
    }

    // Get left or right value or a default alternative
    public L leftOrElse(L alternative) {
        return isLeft() ? left : alternative;
    }

    public R rightOrElse(R alternative) {
        return isRight() ? right : alternative;
    }
}
