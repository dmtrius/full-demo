package com.example.demo.apps;

import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

public interface Loggable {
    Logger LOGGER = Logger.getLogger(
            MethodHandles.lookup().lookupClass().getName());
}
