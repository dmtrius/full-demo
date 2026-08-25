package com.example.demo.apps;

import java.io.Console;
import java.util.Arrays;

public class App55 {
    void main() {
        Console console = System.console();
        if (null == console) {
            IO.println("No console available!");
            return;
        }
        String username = console.readLine("Username: ");
        char[] password = console.readPassword("Password: ");

        IO.println("Username: " + username);
        IO.println("Password: " + Arrays.toString(password));

        Arrays.fill(password, ' ');
    }
}
