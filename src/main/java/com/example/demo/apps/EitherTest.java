package com.example.demo.apps;

public class EitherTest {
    public static void main(String[] args) {
        // Example of dividing two numbers
        Either<String, Integer> result1 = divide(10, 2);  // Right(5)
        Either<String, Integer> result2 = divide(10, 0);  // Left("Division by zero")

        // Pattern matching (using if-else)
        if (result1.isRight()) {
            System.out.println("Success: " + result1.getRight());
        } else {
            System.out.println("Error: " + result1.getLeft());
        }

        if (result2.isRight()) {
            System.out.println("Success: " + result2.getRight());
        } else {
            System.out.println("Error: " + result2.getLeft());
        }

        // Using map and getOrElse
        Either<String, Integer> mappedResult = result1.map(x -> x * 2);  // Right(10)
        System.out.println("Mapped result: " + mappedResult);

        int defaultValue = result2.getOrElse(-1);  // -1
        System.out.println("Default value: " + defaultValue);
        
        Either<String, Integer> flappedResult = Either.right(3);
        Either<String, Integer> flappedMappedResult 
                = flappedResult.flatMap(x -> Either.right(x * 3));
        System.out.println("Flat Mapped result: " + flappedMappedResult);
    }

    public static Either<String, Integer> divide(int a, int b) {
        if (b == 0) {
            return Either.left("Division by zero");
        } else {
            return Either.right(a / b);
        }
    }
}
