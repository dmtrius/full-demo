package com.example.demo.apps;

import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.Stack;

public class AdvancedCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Advanced Calculator");
        System.out.println("Supports: +, -, *, /, and parentheses ()");
        System.out.print("Enter an expression: ");
        String expression = scanner.nextLine();

        try {
            double result = evaluateExpression(expression);
            System.out.println("Result: " + result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Method to evaluate the expression
    public static double evaluateExpression(String expression) {
        var numbers = new ArrayDeque<Double>();
        var operators = new ArrayDeque<Character>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            // Skip whitespace
            if (ch == ' ') continue;

            // Handle numbers
            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder numBuilder = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numBuilder.append(expression.charAt(i));
                    i++;
                }
                i--; // Move back one character to avoid skipping
                numbers.push(Double.parseDouble(numBuilder.toString()));
            }

            // Handle parentheses
            else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    double result = applyOperation(operators.pop(), numbers.pop(), numbers.pop());
                    numbers.push(result);
                }
                operators.pop(); // Pop '('
            }

            // Handle operators
            else if (isOperator(ch)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ch)) {
                    double result = applyOperation(operators.pop(), numbers.pop(), numbers.pop());
                    numbers.push(result);
                }
                operators.push(ch);
            }
        }

        // Evaluate remaining operators
        while (!operators.isEmpty()) {
            double result = applyOperation(operators.pop(), numbers.pop(), numbers.pop());
            numbers.push(result);
        }

        return numbers.pop();
    }

    // Method to check if a character is an operator
    public static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    // Method to determine operator precedence
    public static int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> 0;
        };
    }

    // Method to apply an operation
    public static double applyOperation(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) {
                    throw new ArithmeticException("Division by zero is not allowed.");
                }
                yield a / b;
            }
            default -> 0;
        };
    }
}
