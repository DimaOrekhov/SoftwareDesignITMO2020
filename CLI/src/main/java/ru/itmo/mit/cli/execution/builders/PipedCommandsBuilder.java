package ru.itmo.mit.cli.execution.builders;

import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.PipedCommands;

public interface PipedCommandsBuilder {

    PipedCommandsBuilder addCommand(Command command);

/*
    default PipedCommandsBuilder addCommands(Command...commands) {
        for (Command command: commands) {
            addCommand(command);
        }
        return this;
    }
*/

    PipedCommands build();

}
