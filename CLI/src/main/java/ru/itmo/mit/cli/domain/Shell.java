package ru.itmo.mit.cli.domain;


public interface Shell {

    /**
     * Interprets a command represented as a String
     * @param inputString
     */
    void interpret(String inputString);

}
