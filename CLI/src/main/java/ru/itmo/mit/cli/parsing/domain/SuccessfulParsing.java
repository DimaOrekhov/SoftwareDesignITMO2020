package ru.itmo.mit.cli.parsing.domain;

/**
 * Indicates that parsing has been successful and wraps its result
 * @param <T> type of result
 */
public class SuccessfulParsing<T> implements ParsingResult<T> {

    private final T result;

    public SuccessfulParsing(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
