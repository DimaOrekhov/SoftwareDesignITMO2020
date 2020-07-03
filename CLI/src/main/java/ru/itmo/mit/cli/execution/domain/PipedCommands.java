package ru.itmo.mit.cli.execution.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class representing a chain of piped commands
 */
public abstract class PipedCommands {

    private final List<Command> commandList;

    /**
     * @param commandList List represents piped commands
     */
    protected PipedCommands(List<Command> commandList) {
        this.commandList = commandList;
    }

    public List<Command> getCommandList() {
        return commandList;
    }

    /**
     * Equals requires every command in commandList to be equal
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return this.commandList.equals(((PipedCommands)obj).commandList);
    }

    /**
     * appropriate hashCode
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.commandList);
    }

    /**
     * @return concatenation of command represented as Strings
     * delimited by " | "
     */
    @Override
    public String toString() {
        return commandList.stream()
                        .map(Command::toString)
                        .collect(Collectors.joining(" | "));
    }
}
