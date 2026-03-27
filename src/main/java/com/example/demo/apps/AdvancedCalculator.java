package com.example.demo.apps;

import java.util.ArrayDeque;
import java.util.Scanner;

public class AdvancedCalculator {

    void main() {
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

    public static double evaluateExpression(String expression) {
        var numbers = new ArrayDeque<Double>();
        var operators = new ArrayDeque<Character>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == ' ') {
                continue;
            }

            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder numBuilder = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i))
                        || expression.charAt(i) == '.')) {
                    numBuilder.append(expression.charAt(i));
                    i++;
                }
                i--;
                numbers.push(Double.parseDouble(numBuilder.toString()));
            } else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    double result = applyOperation(operators.pop(), numbers.pop(), numbers.pop());
                    numbers.push(result);
                }
                operators.pop();
            } else if (isOperator(ch)) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ch)) {
                    double result = applyOperation(operators.pop(), numbers.pop(), numbers.pop());
                    numbers.push(result);
                }
                operators.push(ch);
            }
        }

        while (!operators.isEmpty()) {
            double result = applyOperation(operators.pop(), numbers.pop(), numbers.pop());
            numbers.push(result);
        }

        return numbers.pop();
    }

    public static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    public static int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> 0;
        };
    }

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
