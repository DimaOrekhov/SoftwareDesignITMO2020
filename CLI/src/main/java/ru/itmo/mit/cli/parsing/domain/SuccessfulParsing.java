package ru.itmo.mit.cli.parsing.domain;

public class SuccessfulParsing<T> implements ParsingResult<T> {

    private final T result;

    public SuccessfulParsing(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
