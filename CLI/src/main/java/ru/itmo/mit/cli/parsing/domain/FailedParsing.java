package ru.itmo.mit.cli.parsing.domain;

public class FailedParsing<T> implements ParsingResult<T> {

    private final String errorMessage;

    public FailedParsing(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    
}
