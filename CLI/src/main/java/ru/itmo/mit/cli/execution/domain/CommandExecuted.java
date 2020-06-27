package ru.itmo.mit.cli.execution.domain;

/**
 * Class for indicating successful execution of a command
 */
public class CommandExecuted implements CommandExecutionResult {

    private static final CommandExecuted singleton = new CommandExecuted();

    public static CommandExecuted getInstance() {
        return singleton;
    }

}
