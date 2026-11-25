package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

@Slf4j
@SuppressWarnings("preview")
public class App43 {
    void main() {
        m1();
        IO.println(m2());
    }

    private static final LazyConstant<Logger> LOG =
            LazyConstant.of(() -> Logger.getLogger(
                    MethodHandles.lookup().lookupClass().getName()));
    private static final LazyConstant<Logger> LOG2 = LazyConstant.of(() -> Logger.getLogger(App43.class.getName()));

    void m1() {
        LOG.get().info("==== LazyConstant ===");
        LOG2.get().info("==== Another Logger ===");
        log.atInfo().log("==== Logging with SLF4J ===");
    }

    String m2() {
        return Thread.currentThread()
                .getStackTrace()[1]
                .getClassName();
    }
}
