package ru.itmo.mit.cli.parsing.domain;

import ru.itmo.mit.cli.execution.domain.PipedCommands;

public interface CommandParser {

    ParsingResult<PipedCommands> parseCommand(String inputString);
}
