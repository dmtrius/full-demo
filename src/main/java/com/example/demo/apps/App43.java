package com.example.demo.apps;

import java.util.logging.Logger;

import static java.lang.IO.println;

public class App43 {
    void main() {
        println("Arghhh");
        m1();
    }

    private static final LazyConstant<Logger> LOG =
            LazyConstant.of(() -> Logger.getLogger(App43.class.getName()));

    void m1() {
        LOG.get().info("==== LazyConstant ===");
    }
}
