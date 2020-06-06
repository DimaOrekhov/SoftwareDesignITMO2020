package ru.itmo.mit.cli.execution.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class PipedCommands {

    private final List<Command> commandList;

    protected PipedCommands(List<Command> commandList) {
        this.commandList = commandList;
    }

    public List<Command> getCommandList() {
        return commandList;
    }

    @Override
    public boolean equals(Object obj) {
        return this.commandList.equals(((PipedCommands)obj).commandList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.commandList);
    }

    @Override
    public String toString() {
        return String.join(" | ",
                commandList.stream()
                        .map(Command::toString)
                        .collect(Collectors.toList()));
    }
}
