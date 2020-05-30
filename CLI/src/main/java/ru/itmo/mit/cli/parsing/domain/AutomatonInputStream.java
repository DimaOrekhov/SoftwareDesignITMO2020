package ru.itmo.mit.cli.parsing.domain;

public interface AutomatonInputStream<T> extends Iterable<T> {

    void rollBack();

    default void rollBackN(int n) {
        for (int i = 0; i < n; i++) {
            rollBack();
        }
    }
}
