package ru.itmo.mit.cli.parsing.domain;

import ru.itmo.mit.cli.execution.domain.PipedCommands;

/**
 * Interface for a command parser
 */
public interface CommandParser {

    /**
     * @param inputString String representation of a command
     * @return Chain of piped commands wrapped into ParsingResult class
     */
    ParsingResult<PipedCommands> parseCommand(String inputString);
}
