package ru.itmo.mit.cli.execution.builders;

import ru.itmo.mit.cli.execution.*;
import ru.itmo.mit.cli.execution.commands.*;
import ru.itmo.mit.cli.execution.domain.Command;

import java.util.LinkedList;
import java.util.List;

public class CommandBuilderImpl implements CommandBuilder {

    private String commandName;
    private final List<String> commandArgs;

    public CommandBuilderImpl() {
        commandName = "";
        commandArgs = new LinkedList<>();
    }

    @Override
    public CommandBuilder setCommandName(String commandName) {
        this.commandName = commandName;
        return this;
    }

    @Override
    public CommandBuilder addArgument(String arg) {
        commandArgs.add(arg);
        return this;
    }

    @Override
    public Command build() {
        switch (CommandType.fromString(commandName)) {
            case ASSIGN:
                return new AssignmentCommand(commandArgs);
            case CAT:
                return new CatCommand(commandArgs);
            case ECHO:
                return new EchoCommand(commandArgs);
            case WC:
                return new WcCommand(commandArgs);
            case PWD:
                return new PwdCommand(commandArgs);
            case EXIT:
                return new ExitCommand(commandArgs);
            default:
                return new OtherCommand(commandName, commandArgs);
        }
    }
}
