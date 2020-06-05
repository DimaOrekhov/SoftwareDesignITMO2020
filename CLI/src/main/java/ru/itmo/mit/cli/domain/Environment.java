package ru.itmo.mit.cli.domain;

import ru.itmo.mit.cli.execution.domain.PipedCommands;

public interface Environment {

    String getNextLine();

    void feedLine(String line);

    void modifyNamespace(String varName, String varValue);

    void executeCommands(PipedCommands commands);

    void println(String text);
}
