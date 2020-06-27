package ru.itmo.mit.cli.execution.domain;

/**
 * Indicates that command couldn't be executed properly
 * Wraps error message
 */
public class FailedToExecute implements CommandExecutionResult {

    private final String errorMessage;

    public FailedToExecute(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
