package com.example.demo.apps;

public class App56 {
    void main() {
        IO.println("Hello!");
        R1 r1 = new R1(1, "Alice");
        IO.println(r1);
        R1 r2 = r1.withName("Bob");
        IO.println(r2);
        R1 r3 = r1.withId(2);
        IO.println(r3);
    }
}

record R1(int id, String name) {
    public R1 withId(int id) {
        return new R1(id, name);
    }
    public R1 withName(String newName) {
        return new R1(id, newName);
    }
}
