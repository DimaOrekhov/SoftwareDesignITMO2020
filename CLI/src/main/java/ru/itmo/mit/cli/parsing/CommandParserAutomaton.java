package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.execution.domain.PipedCommands;
import ru.itmo.mit.cli.parsing.domain.Automaton;
import ru.itmo.mit.cli.parsing.domain.CommandParser;
import ru.itmo.mit.cli.parsing.domain.CommandToken;

public class CommandParserAutomaton extends Automaton<Character, CommandToken> implements CommandParser {

    @Override
    public PipedCommands parseCommand(String inputString) {
        return null;
    }

    @Override
    protected AutomatonState getStartingState() {
        return null;
    }


}
