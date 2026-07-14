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

    static void main() {
        int x = 10;
        int y = 5;
        for (Operation op : Operation.values()) {
            IO.println(String.format("%d %s %d = %d", x, op.name(), y, op.apply(x, y)));
        }
        IO.println(Operation.DIVIDE.apply(10, 0));
    }
}
