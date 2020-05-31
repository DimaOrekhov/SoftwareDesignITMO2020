package ru.itmo.mit.cli.execution.builders;

import ru.itmo.mit.cli.execution.CommandImpl;
import ru.itmo.mit.cli.execution.domain.Command;

import java.util.LinkedList;
import java.util.List;

public class CommandBuilderImpl implements CommandBuilder {

    private String commandName;
    private final List<String> commandArgs;

    public CommandBuilderImpl() {
        commandName = null;
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
        return new CommandImpl(commandName, commandArgs);
    }
}
