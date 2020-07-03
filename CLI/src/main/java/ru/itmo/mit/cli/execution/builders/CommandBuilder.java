package ru.itmo.mit.cli.execution.builders;

import ru.itmo.mit.cli.execution.domain.Command;
import ru.itmo.mit.cli.execution.domain.CommandWord;

/**
 * CommandBuilder interface, allows to add command arguments one by one
 */
public interface CommandBuilder {

    CommandBuilder setCommandName(CommandWord commandName);

    CommandBuilder addArgument(CommandWord arg);

    default CommandBuilder addArguments(CommandWord...args) {
        for (CommandWord arg: args) {
            addArgument(arg);
        }
        return this;
    }

    Command build();
}
