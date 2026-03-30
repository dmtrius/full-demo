package com.example.demo.apps;

import java.util.logging.Logger;

public class App43 {
    void main() {
        m1();
    }

    private static final LazyConstant<Logger> LOG =
            LazyConstant.of(() -> Logger.getLogger(App43.class.getName()));

    void m1() {
        LOG.get().info("==== LazyConstant ===");
    }
}
