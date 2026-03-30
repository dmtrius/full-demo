package com.example.demo.apps;

import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import static java.lang.IO.println;

@Slf4j
public class App44 {

    private static final Logger LOGGER = Logger.getLogger(
            MethodHandles.lookup().lookupClass().getName());

    void main() {
        println("Arghhhh");
        log.info("lombok: Whooo");
        LOGGER.info("LOGGER: blyaaa");
    }
}
