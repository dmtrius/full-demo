package com.example.demo.apps.tasks;

public enum TrafficLight {
    RED {
        @Override
        public TrafficLight next() { return GREEN; }
        @Override
        public String action() { return "Stop"; }
    },
    GREEN {
        @Override
        public TrafficLight next() { return YELLOW; }
        @Override
        public String action() { return "Go"; }
    },
    YELLOW {
        @Override
        public TrafficLight next() { return RED; }
        @Override
        public String action() { return "Caution"; }
    };

    public abstract TrafficLight next();
    public abstract String action();

    static void main() {
        TrafficLight light = RED;
        for (int i = 0; i < 6; i++) {  // Cycle through a few times.
            IO.println("Light: %s, Action: %s".formatted(light, light.action()));
            light = light.next();
        }
    }
}
