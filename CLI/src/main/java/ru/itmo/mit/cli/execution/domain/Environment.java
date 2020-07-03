package ru.itmo.mit.cli.execution.domain;

import java.nio.charset.Charset;
import java.nio.file.Path;

/**
 * Environment interface
 * All commands are executed inside an Environment
 */
public interface Environment {

    /**
     * @return current working directory of an Environment
     */
    Path getWorkingDirectory();

    /**
     * @return character encoding used in an Environment
     */
    Charset getCharset();

    /**
     * Provides access to Environment's underlying Namespace
     * @return
     */
    Namespace getNamespace();

    /**
     * Bind variable name with some value
     * @param varName variable name
     * @param varValue associated value
     */
    void modifyNamespace(String varName, String varValue);

    /**
     * @param commands chain of piped commands to be executed
     */
    void executeCommands(PipedCommands commands);

    /**
     * Print text to environment
     * @param text
     */
    void println(String text);

}
