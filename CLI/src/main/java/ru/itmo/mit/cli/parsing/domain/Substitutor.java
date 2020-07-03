package ru.itmo.mit.cli.parsing.domain;

/**
 * Substitutor substitutes variable names with associated values
 */
public interface Substitutor {

    /**
     * @param inputString String which possibly contains variables
     * @return String with values substituted for variable names
     */
    ParsingResult<String> substitute(String inputString);

}
