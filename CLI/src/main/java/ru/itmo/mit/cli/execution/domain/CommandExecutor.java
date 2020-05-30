package ru.itmo.mit.cli.execution.domain;

public interface CommandExecutor {

    void execute(PipedCommands commands);
}
