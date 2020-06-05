package ru.itmo.mit.cli.execution.domain;

public class FailedToExecute implements CommandExecutionResult {

    private final String errorMessage;

    public FailedToExecute(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
