package ru.itmo.mit.cli.execution.builders;

import ru.itmo.mit.cli.execution.domain.Command;

public interface CommandBuilder {

    CommandBuilder setCommandName(String commandName);

    CommandBuilder addArgument(String arg);

    default CommandBuilder addArguments(String...args) {
        for (String arg: args) {
            addArgument(arg);
        }
        return this;
    }

    Command build();
}
