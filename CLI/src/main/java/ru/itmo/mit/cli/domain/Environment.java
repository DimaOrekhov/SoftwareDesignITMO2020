package ru.itmo.mit.cli.domain;

public interface Environment extends Iterable<String> {

    String getNextLine();

    void feedLine(String line);

    void modifyNamespace(String varName, String varValue);

}
