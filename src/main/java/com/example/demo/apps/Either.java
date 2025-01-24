package com.example.demo.apps;

import java.util.Objects;
import java.util.function.Function;

public class Either<L, R> {
    private final L left;
    private final R right;
    private final boolean isLeft;

    private Either(L left, R right, boolean isLeft) {
        this.left = left;
        this.right = right;
        this.isLeft = isLeft;
    }

    // Factory method for creating a Left value
    public static <L, R> Either<L, R> left(L value) {
        return new Either<>(value, null, true);
    }

    // Factory method for creating a Right value
    public static <L, R> Either<L, R> right(R value) {
        return new Either<>(null, value, false);
    }

    // Check if this is a Left value
    public boolean isLeft() {
        return isLeft;
    }

    // Check if this is a Right value
    public boolean isRight() {
        return !isLeft;
    }

    // Get the Left value (throws an exception if this is a Right)
    public L getLeft() {
        if (!isLeft) {
            throw new IllegalStateException("Cannot get Left value from a Right");
        }
        return left;
    }

    // Get the Right value (throws an exception if this is a Left)
    public R getRight() {
        if (isLeft) {
            throw new IllegalStateException("Cannot get Right value from a Left");
        }
        return right;
    }

    // Map the Right value (if it exists)
    public <T> Either<L, T> map(Function<R, T> mapper) {
        if (isRight()) {
            return Either.right(mapper.apply(right));
        } else {
            return Either.left(left);
        }
    }

    // FlatMap the Right value (if it exists)
    public <T> Either<L, T> flatMap(Function<R, Either<L, T>> mapper) {
        if (isRight()) {
            return mapper.apply(right);
        } else {
            return Either.left(left);
        }
    }

    // Get the Right value or a default value if this is a Left
    public R getOrElse(R defaultValue) {
        return isRight() ? right : defaultValue;
    }

    @Override
    public String toString() {
        return isLeft ? "Left(" + left + ")" : "Right(" + right + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Either<?, ?> either = (Either<?, ?>) o;
        return Objects.equals(left, either.left) && Objects.equals(right, either.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
