package com.example.demo.apps.tasks;

public enum Operation {
    ADD {
        @Override
        public int apply(int a, int b) { return a + b; }
    },
    SUBTRACT {
        @Override
        public int apply(int a, int b) { return a - b; }
    },
    MULTIPLY {
        @Override
        public int apply(int a, int b) { return a * b; }
    },
    DIVIDE {
        @Override
        public int apply(int a, int b) { return a / b; }
    };

    public abstract int apply(int a, int b);

    public static void main(String[] args) {
        int x = 10, y = 5;
        for (Operation op : Operation.values()) {
            System.out.printf("%d %s %d = %d%n", x, op.name(), y, op.apply(x, y));
        }
        System.out.println(Operation.DIVIDE.apply(10, 0));
    }
}
