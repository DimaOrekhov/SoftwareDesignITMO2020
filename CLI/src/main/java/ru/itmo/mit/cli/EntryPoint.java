package ru.itmo.mit.cli;

import ru.itmo.mit.cli.execution.EnvironmentImpl;
import ru.itmo.mit.cli.execution.NamespaceImpl;
import ru.itmo.mit.cli.execution.domain.Namespace;
import ru.itmo.mit.cli.domain.Shell;
import ru.itmo.mit.cli.parsing.CommandParserAutomaton;
import ru.itmo.mit.cli.parsing.SubstitutionAutomaton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EntryPoint {

    private static final Charset charset = StandardCharsets.UTF_8;

    public static void main(String[] args) {
        Path rootDir = Paths.get(System.getProperty("user.dir"));
        Namespace namespace = new NamespaceImpl();
        Shell shell = new ShellImpl(new SubstitutionAutomaton(namespace),
                new CommandParserAutomaton(),
                new EnvironmentImpl(rootDir, namespace, System.out, charset));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in,
                charset));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                shell.interpret(line);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("IO fail");
        }
    }

}
