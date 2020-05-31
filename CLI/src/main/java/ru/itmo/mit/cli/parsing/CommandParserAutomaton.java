package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.execution.builders.PipedCommandsBuilderImpl;
import ru.itmo.mit.cli.execution.domain.PipedCommands;
import ru.itmo.mit.cli.execution.builders.PipedCommandsBuilder;
import ru.itmo.mit.cli.parsing.domain.*;

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
        return factory.getSpaceConsumingStartingState();
    }

/*    public static boolean characterIsCommandSymbol(Character symbol) {
        return Character.isDigit(symbol) ||
                Character.isLetter(symbol) ||
                symbol == '=';
    }*/

    private class ComParserAutoStateFactory {

        private final SpaceConsumingStartingState spaceConsumingStartingState;
        private final SpaceConsumingState spaceConsumingState;
        private final BasicCommandParsingState basicCommandParsingState;
        private final BasicArgumentParsingState basicArgumentParsingState;
        private final InsideQuotes insideQuotes;
        private final PipeState pipeState;
        private final FinalState finalState;

        private ComParserAutoStateFactory() {
            spaceConsumingStartingState = new SpaceConsumingStartingState();
            spaceConsumingState = new SpaceConsumingState();
            basicCommandParsingState = new BasicCommandParsingState();
            basicArgumentParsingState = new BasicArgumentParsingState();
            insideQuotes = new InsideQuotes();
            pipeState = new PipeState();
            finalState = new FinalState();
        }

        public SpaceConsumingStartingState getSpaceConsumingStartingState() {
            return spaceConsumingStartingState;
        }

        public SpaceConsumingState getSpaceConsumingState() {
            return spaceConsumingState;
        }

        public BasicCommandParsingState getBasicCommandParsingState() {
            return basicCommandParsingState;
        }

        public BasicArgumentParsingState getBasicArgumentParsingState() {
            return basicArgumentParsingState;
        }

        public InsideQuotes getInsideQuotes() {
            return insideQuotes;
        }

        public PipeState getPipeState() {
            return pipeState;
        }

        public FinalState getFinalState() {
            return finalState;
        }

    }

    private class SpaceConsumingStartingState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            for (Character symbol : inStream) {
                if (Character.isSpaceChar(symbol)) {
                    continue;
                }
                inStream.rollBack();
                return new AutomatonStateStepResult(factory.getBasicCommandParsingState(),
                        CommandToken.getEmptyToken());
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    CommandToken.getEmptyToken());
        }
    }

    private class SpaceConsumingState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            for (Character symbol: inStream) {
                if (Character.isSpaceChar(symbol)) {
                    continue;
                }
                inStream.rollBack();
                return new AutomatonStateStepResult(factory.getBasicArgumentParsingState(),
                        CommandToken.getEmptyToken());
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    CommandToken.getEmptyToken());
        }
    }

    private class BasicCommandParsingState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder commandNameBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (Character.isSpaceChar(symbol)) {
                    return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                            finalize(commandNameBuilder));
                }
                if (symbol == '\'' || symbol == '"') {
                    return new AutomatonStateStepResult(factory.getInsideQuotes(),
                            finalize(commandNameBuilder));
                }
                if (symbol == '|') {
                    return new AutomatonStateStepResult(factory.getPipeState(),
                            finalize(commandNameBuilder));
                }
                commandNameBuilder.append(symbol);
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    finalize(commandNameBuilder));
        }

        private CommandToken finalize(StringBuilder commandNameBuilder) {
            return new CommandToken(commandNameBuilder.toString(), CommandTokenType.COMMAND);
        }
    }

    private class BasicArgumentParsingState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder argBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (Character.isSpaceChar(symbol)) {
                    return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                            finalize(argBuilder));
                }
                if (symbol == '\'' || symbol == '"') {
                    return new AutomatonStateStepResult(factory.getInsideQuotes(),
                            finalize(argBuilder));
                }
                if (symbol == '|') {
                    return new AutomatonStateStepResult(factory.getPipeState(),
                            finalize(argBuilder));
                }
                argBuilder.append(symbol);
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    finalize(argBuilder));
        }

        private CommandToken finalize(StringBuilder argBuilder) {
            return new CommandToken(argBuilder.toString(), CommandTokenType.ARGUMENT);
        }
    }

    private class InsideQuotes extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder argBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                // Вроде, обещали, что вложенных кавычек не встречается
                if (symbol == '"' || symbol == '\'') {
                    return new AutomatonStateStepResult(factory.getBasicArgumentParsingState(),
                            finalize(argBuilder));
                }
                argBuilder.append(symbol);
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    finalize(argBuilder));
        }

        private CommandToken finalize(StringBuilder argBuilder) {
            return new CommandToken(argBuilder.toString(), CommandTokenType.ARGUMENT);
        }
    }

    private class PipeState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return new AutomatonStateStepResult(factory.getSpaceConsumingStartingState(),
                    new CommandToken("|", CommandTokenType.PIPE));
        }
    }

    private class FinalState extends TerminalState {
        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return null;
        }
    }
}
