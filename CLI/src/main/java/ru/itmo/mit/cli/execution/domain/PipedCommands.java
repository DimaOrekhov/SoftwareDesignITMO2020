package ru.itmo.mit.cli.execution.domain;

import java.util.List;

public abstract class PipedCommands {

    private final List<Command> commandList;

    protected PipedCommands(List<Command> commandList) {
        this.commandList = commandList;
    }

    public abstract void execute();

    @Override
    public boolean equals(Object obj) {
        return this.commandList.equals(((PipedCommands)obj).commandList);
    }
}
