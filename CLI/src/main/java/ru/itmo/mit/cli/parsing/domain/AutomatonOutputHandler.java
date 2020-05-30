package ru.itmo.mit.cli.parsing.domain;

public interface AutomatonOutputHandler<T> {

    void handle(T stateResult);
}
