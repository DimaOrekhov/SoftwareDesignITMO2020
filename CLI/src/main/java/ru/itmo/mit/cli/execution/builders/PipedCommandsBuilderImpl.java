package ru.itmo.mit.cli.execution.builders;

import ru.itmo.mit.cli.execution.PipedCommandsImpl;
import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.PipedCommands;

import java.util.LinkedList;
import java.util.List;

public class PipedCommandsBuilderImpl implements PipedCommandsBuilder {

    private final List<Command> commands;

    public PipedCommandsBuilderImpl() {
        commands = new LinkedList<>();
    }

    @Override
    public PipedCommandsBuilder addCommand(Command command) {
        commands.add(command);
        return this;
    }

    @Override
    public PipedCommands build() {
        return new PipedCommandsImpl(commands);
    }
}
