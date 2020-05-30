package ru.itmo.mit.cli;

import ru.itmo.mit.cli.domain.Namespace;
import ru.itmo.mit.cli.domain.Shell;
import ru.itmo.mit.cli.execution.CommandExecutorImpl;
import ru.itmo.mit.cli.parsing.CommandParserAutomaton;
import ru.itmo.mit.cli.parsing.SubstitutionAutomaton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EntryPoint {

    public static void main() {
        Namespace namespace = new NamespaceImpl();
        Shell shell = new ShellImpl(new SubstitutionAutomaton(namespace),
                new CommandParserAutomaton(),
                new CommandExecutorImpl());
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
