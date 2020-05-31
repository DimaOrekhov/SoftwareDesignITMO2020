package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.execution.builders.PipedCommandsBuilderImpl;
import ru.itmo.mit.cli.execution.domain.PipedCommandBuilder;
import ru.itmo.mit.cli.execution.domain.PipedCommands;
import ru.itmo.mit.cli.execution.builders.PipedCommandsBuilder;
import ru.itmo.mit.cli.parsing.domain.Automaton;
import ru.itmo.mit.cli.parsing.domain.AutomatonInputStream;
import ru.itmo.mit.cli.parsing.domain.CommandParser;
import ru.itmo.mit.cli.parsing.domain.CommandToken;

public final class CommandParserAutomaton extends Automaton<Character, CommandToken> implements CommandParser {

    private final ComParserAutoStateFactory factory;

    public CommandParserAutomaton() {
        factory = new ComParserAutoStateFactory();
    }

    @Override
    public PipedCommands parseCommand(String inputString) {
        PipedCommandsBuilder commandBuilder = new PipedCommandsBuilderImpl();
        process(new StringPosStream(inputString),
                new PipedCommandsBuilderAsHandler(commandBuilder));
        return commandBuilder.build();
    }

    @Override
    protected AutomatonState getStartingState() {
        return factory.getBasicState();
    }

    private class ComParserAutoStateFactory {

        private final BasicState basicState;
        private final InsideQuotes insideQuotes;
        private final FinalState finalState;

        private ComParserAutoStateFactory() {
            basicState = new BasicState();
            insideQuotes = new InsideQuotes();
            finalState = new FinalState();
        }

        public BasicState getBasicState() {
            return basicState;
        }

        public InsideQuotes getInsideQuotes() {
            return insideQuotes;
        }

        public FinalState getFinalState() {
            return finalState;
        }

    }

    private class BasicState implements NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return null;
        }
    }

    private class InsideQuotes implements NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return null;
        }
    }

    private class FinalState implements TerminalState {
        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return null;
        }
    }
}
