package com.example.demo.apps.tasks;

import static java.lang.IO.println;

public class EitherExamples {
    void main() {
        example();
    }

    void example() {
        Either<String, Integer> result = Either.predicate(
                () -> {
                    try {
                        Integer.parseInt("123");
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                },
                () -> "Not a number",
                () -> Integer.parseInt("123")
        );

        //result.ifRight(value -> println("Parsed number: " + value));
        result.ifLeft(error -> println("Error: " + error));
    }
}
