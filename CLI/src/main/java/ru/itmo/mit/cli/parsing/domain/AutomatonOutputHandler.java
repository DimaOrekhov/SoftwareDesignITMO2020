package ru.itmo.mit.cli.parsing.domain;

/**
 * Handles results of automaton's processing at each step
 * @param <T> type of automaton's processing result
 */
public interface AutomatonOutputHandler<T> {

    /**
     * Supposed to be called after every step
     * @param stateResult
     */
    void handle(T stateResult);

    /**
     * This method should be called after handling of the last step result.
     */
    void finalizeHandler();
}
