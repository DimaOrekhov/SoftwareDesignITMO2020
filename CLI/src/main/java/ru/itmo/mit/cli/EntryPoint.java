package ru.itmo.mit.cli;

import ru.itmo.mit.cli.domain.Shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EntryPoint {

    public static void main() {
        Shell shell = new ShellImpl();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in,
                StandardCharsets.UTF_8));
        try {
            while (reader.ready()) {
                shell.interpret(reader.readLine());
            }
        }
        catch (IOException e) {
            throw new RuntimeException("IO fail");
        }
    }

}
