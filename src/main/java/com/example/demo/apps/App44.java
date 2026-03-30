package com.example.demo.apps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App44 implements Loggable {

    private static final Logger log = LoggerFactory.getLogger(App44.class.getName());

    void main() {
        LOGGER.info("WTF?");
        m1();
        m2();
        log.info("SLF4J - WTF?");
    }

    void m1() {
        LOGGER.info("m1() - WTF?");
    }

    static void m2() {
        LOGGER.info("m2() - WTF?");
    }
}
