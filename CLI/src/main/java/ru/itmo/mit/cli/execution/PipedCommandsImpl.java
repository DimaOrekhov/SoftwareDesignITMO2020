package ru.itmo.mit.cli.execution;

import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.PipedCommands;

import java.util.List;

/**
 * Simple implementation of PipedCommands abstract class
 */
public class PipedCommandsImpl extends PipedCommands {

    public PipedCommandsImpl(List<Command> commandList) {
        super(commandList);
    }

}
