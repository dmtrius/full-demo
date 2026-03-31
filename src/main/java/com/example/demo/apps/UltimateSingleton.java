package com.example.demo.apps;

import java.io.Serial;
import java.io.Serializable;

@SuppressWarnings("unused")
public class UltimateSingleton implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("all")
    private UltimateSingleton() {
        // prevent reflection
        if (Holder.INSTANCE != null) {
            throw new RuntimeException("Reflection not allowed");
        }
    }

    private static class Holder {
        private static final UltimateSingleton INSTANCE = new UltimateSingleton();
    }

    public static UltimateSingleton getInstance() {
        return Holder.INSTANCE;
    }

    @Serial
    public Object readResolve() {
        return Holder.INSTANCE;
    }
}
