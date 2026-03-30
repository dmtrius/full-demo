package com.example.demo.apps;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import static java.lang.IO.println;

public class App43 {
    void main() {
        m1();
        println(m2());
    }

    private static final LazyConstant<Logger> LOG =
            LazyConstant.of(() -> Logger.getLogger(
                    MethodHandles.lookup().lookupClass().getName()));

    void m1() {
        LOG.get().info("==== LazyConstant ===");
    }

    String m2() {
        return Thread.currentThread()
                .getStackTrace()[1]
                .getClassName();
    }
}
