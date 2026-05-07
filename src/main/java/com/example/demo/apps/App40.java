package com.example.demo.apps;

import static java.lang.IO.println;

public class App40 {
    void main() {
        String str = "Hello, World!";
        int length = m1(str);
        println("Length of the string: " + length);
    }

    public int m1(String str) {
        return str.length();
    }
}
