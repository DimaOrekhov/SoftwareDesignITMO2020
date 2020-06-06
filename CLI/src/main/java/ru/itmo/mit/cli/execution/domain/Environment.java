package ru.itmo.mit.cli.execution.domain;

import java.nio.charset.Charset;
import java.nio.file.Path;

public interface Environment {

    Path getWorkingDirectory();

    Charset getCharset();

    void modifyNamespace(String varName, String varValue);

    void executeCommands(PipedCommands commands);

    void println(String text);
}
