package com.example.demo.apps.tasks;

public enum Command {
    START {
        @Override
        public void execute() {
            IO.println("System is starting...");
        }
    },
    STOP {
        @Override
        public void execute() {
            IO.println("System is stopping...");
        }
    },
    RESTART {
        @Override
        public void execute() {
            IO.println("System is restarting...");
        }
    };

    public abstract void execute();

    // A helper to parse a string into a Command.
    public static Command fromString(String commandStr) {
        try {
            return Command.valueOf(commandStr.toUpperCase());
        } catch (IllegalArgumentException _) {
            throw new UnsupportedOperationException("Unsupported command: " + commandStr);
        }
    }

    static void main() {
        // Imagine this comes from user input or configuration:
        String input = "start";
        Command cmd = Command.fromString(input);
        cmd.execute();
        Command cmd2 = Command.fromString("stop");
        cmd2.execute();
    }
}
