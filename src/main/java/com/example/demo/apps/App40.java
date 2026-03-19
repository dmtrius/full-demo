package com.example.demo.apps;

import static java.lang.IO.println;

public class App40 {
    void main() {
        m1(null);
    }

    void m1(CharSequence s) {
        println("CharSequence");
    }

    void m1(String s) {
        println("String");
    }

    void m1(Object s) {
        println("Object");
    }
}
