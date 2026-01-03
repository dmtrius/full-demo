package com.example.demo.apps.algo;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;

import static java.lang.IO.print;
import static java.lang.IO.println;

public class RPNCalculator {
    public static double evaluate(String input) {
        Deque<Double> stack = new LinkedList<>();
        String[] tokens = input.trim().split("\\s+");

        for (String token : tokens) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                double op2 = stack.pop();
                double op1 = stack.pop();
                stack.push(apply(token.charAt(0), op1, op2));
            }
        }
        return stack.pop();
    }

    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException _) {
            return false;
        }
    }

    private static double apply(char op, double op1, double op2) {
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
        print("Enter RPN expression (space-separated): ");
        String line = scanner.nextLine();
        try {
            double result = evaluate(line);
            println("Result: " + result);
        } catch (Exception e) {
            println("Error: " + e.getMessage());
        }
        scanner.close();
    }
}
