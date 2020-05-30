package ru.itmo.mit.cli.parsing.domain;

public interface AutomatonInputStream<T> extends Iterable<T> {

    public void rollBack();
}
