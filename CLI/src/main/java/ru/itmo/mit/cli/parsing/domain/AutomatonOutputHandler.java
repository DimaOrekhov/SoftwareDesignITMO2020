package ru.itmo.mit.cli.parsing.domain;

public interface AutomatonOutputHandler<T> {

    /**
     * Supposed to be called after every step
     * @param stateResult
     */
    void handle(T stateResult);

    void finalizeHandler();
}
