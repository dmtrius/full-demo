package com.example.demo.apps.algo;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;

import static java.lang.IO.print;
import static java.lang.IO.println;

public class PolishNotationCalculator {
    private final Deque<Double> stack = new LinkedList<>();

    public double evaluate(String[] tokens) {
        for (int i = tokens.length - 1; i >= 0; i--) {
            String token = tokens[i];
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                double op2 = stack.pop();
                double op1 = stack.pop();
                stack.push(applyOperator(token.charAt(0), op1, op2));
            }
        }
        return stack.pop();
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }

    private double applyOperator(char op, double op1, double op2) {
        return switch (op) {
            case '+' -> op1 + op2;
            case '-' -> op1 - op2;
            case '*' -> op1 * op2;
            case '/' -> op1 / op2;
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };
    }

    void main() {
        Scanner scanner = new Scanner(System.in);
        print("Enter Polish notation expression (space-separated): ");
        String line = scanner.nextLine();
        String[] tokens = line.split("\\s+");

        PolishNotationCalculator calc = new PolishNotationCalculator();
        try {
            double result = calc.evaluate(tokens);
            println("Result: " + result);
        } catch (Exception e) {
            println("Error: " + e.getMessage());
        }
        scanner.close();
    }
}
