package ru.itmo.mit.cli.parsing.domain;

/**
 * Simple interface representing input stream that
 * feeds into automaton
 * @param <T> type of an input token
 */
public interface AutomatonInputStream<T> extends Iterable<T> {

    void rollBack();

    default void rollBackN(int n) {
        for (int i = 0; i < n; i++) {
            rollBack();
        }
    }
}
