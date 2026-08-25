package com.example.demo.apps;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.Console;
import java.io.IOException;
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

        String pass = JLinePasswordReader.readPassword("Password (JLine): ");
        IO.println(">>> Password (JLine): " + pass);
    }
}

@Slf4j
@UtilityClass
class JLinePasswordReader {
    public static String readPassword(String prompt) {
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {
            LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
            // '*' shows a mask glyph; use '\0' (null char) to suppress echo entirely
            return reader.readLine(prompt, (char) 0);
        } catch (IOException e) {
            log.error("Error reading password", e);
            throw new RuntimeException(e);
        }
    }
}
