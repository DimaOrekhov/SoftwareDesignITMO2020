package ru.itmo.mit.cli;

import ru.itmo.mit.cli.domain.Shell;
import ru.itmo.mit.cli.execution.domain.CommandExecutor;
import ru.itmo.mit.cli.execution.domain.PipedCommands;
import ru.itmo.mit.cli.parsing.domain.CommandParser;
import ru.itmo.mit.cli.parsing.domain.Substitutor;

public class ShellImpl implements Shell {

    private final Substitutor substitutor;
    private final CommandParser commandParser;
    private final CommandExecutor commandExecutor;

    public ShellImpl(Substitutor substitutor,
                     CommandParser commandParser,
                     CommandExecutor commandExecutor) {
        this.substitutor = substitutor;
        this.commandParser = commandParser;
        this.commandExecutor = commandExecutor;
    }

    public void interpret(String inputString) {
        String commandString = substitutor.substitute(inputString);
        PipedCommands commands = commandParser.parseCommand(commandString);
        commandExecutor.execute(commands);
    }

}
