package ru.itmo.mit.cli.parsing.domain;

/**
 * Indicates that an error occurred during parsing
 * Wraps some error message
 * @param <T> type parameter of parsing result
 */
public class FailedParsing<T> implements ParsingResult<T> {

    private final String errorMessage;

    public FailedParsing(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    
}
