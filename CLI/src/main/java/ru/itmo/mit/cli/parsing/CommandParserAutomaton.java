package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.execution.builders.PipedCommandsBuilderImpl;
import ru.itmo.mit.cli.execution.domain.PipedCommands;
import ru.itmo.mit.cli.execution.builders.PipedCommandsBuilder;
import ru.itmo.mit.cli.parsing.domain.*;

/**
 * Implementation of CommandParser based on Automaton abstract class
 */
public final class CommandParserAutomaton extends Automaton<Character, CommandToken> implements CommandParser {

    private final ComParserAutoStateFactory factory;

    public CommandParserAutomaton() {
        factory = new ComParserAutoStateFactory();
    }

    /**
     * Processes input with underlying automaton
     * Wraps result with reached terminal state
     * @param inputString String representation of a command
     * @return
     */
    @Override
    public ParsingResult<PipedCommands> parseCommand(String inputString) {
        PipedCommandsBuilder commandBuilder = new PipedCommandsBuilderImpl();
        TerminalState terminalState = process(new StringPosStream(inputString),
                new PipedCommandsBuilderAsHandler(commandBuilder));
        return terminalState.wrapResult(commandBuilder.build());
    }

    @Override
    protected AutomatonState getStartingState() {
        return factory.getSpaceConsumingStartingState();
    }

    /**
     * @param stringBuilder contains command name
     * @return CommandToken of type COMMAND with name from stringBuilder
     */
    private CommandToken finalizeAsCommand(StringBuilder stringBuilder) {
        return new CommandToken(stringBuilder.toString(),
                CommandTokenType.COMMAND);
    }

    /**
     * @param stringBuilder contains argument name
     * @return CommandToken of type Argument with name from stringBuilder
     */
    private CommandToken finalizeAsArgument(StringBuilder stringBuilder) {
        return new CommandToken(stringBuilder.toString(),
                CommandTokenType.ARGUMENT);
    }

    /**
     * Factory class, provides states of the automaton, each possible state is a singleton
     */
    private class ComParserAutoStateFactory {

        private final SpaceConsumingStartingState spaceConsumingStartingState;
        private final SpaceConsumingState spaceConsumingState;
        private final BasicCommandParsingState basicCommandParsingState;
        private final AssignmentCommandState assignmentCommandState;
        private final BasicArgumentParsingState basicArgumentParsingState;
        private final InsideQuotesState insideSingleQuotesState;
        private final InsideQuotesState insideDoubleQuotesState;
        private final PipeState pipeState;
        private final UnmatchedQuotesFinalState unmatchedState;
        private final FinalState finalState;

        private ComParserAutoStateFactory() {
            spaceConsumingStartingState = new SpaceConsumingStartingState();
            spaceConsumingState = new SpaceConsumingState();
            basicCommandParsingState = new BasicCommandParsingState();
            assignmentCommandState = new AssignmentCommandState();
            basicArgumentParsingState = new BasicArgumentParsingState();
            insideSingleQuotesState = new InsideQuotesState('\'');
            insideDoubleQuotesState = new InsideQuotesState('"');
            pipeState = new PipeState();
            unmatchedState = new UnmatchedQuotesFinalState();
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

        public AssignmentCommandState getAssignmentCommandState() {
            return assignmentCommandState;
        }

        public BasicArgumentParsingState getBasicArgumentParsingState() {
            return basicArgumentParsingState;
        }

        public InsideQuotesState getInsideSingleQuotesState() {
            return insideSingleQuotesState;
        }

        public InsideQuotesState getInsideDoubleQuotesState() {
            return insideDoubleQuotesState;
        }

        public PipeState getPipeState() {
            return pipeState;
        }

        public UnmatchedQuotesFinalState getUnmatchedState() {
            return unmatchedState;
        }

        public FinalState getFinalState() {
            return finalState;
        }

    }

    /**
     * Starting state of CommandParsingAutomaton
     * Skips through any space characters until non-space character is met.
     * Then transition to BasicCommandParsingState occurs
     */
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

    /**
     * Consumes space characters between arguments
     */
    private class SpaceConsumingState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            for (Character symbol: inStream) {
                if (Character.isSpaceChar(symbol)) {
                    continue;
                }
                if (symbol == '|') {
                    return new AutomatonStateStepResult(factory.getPipeState(),
                            CommandToken.getEmptyToken());
                }
                if (symbol == '\'') {
                    return new AutomatonStateStepResult(factory.getInsideSingleQuotesState(),
                            CommandToken.getEmptyToken());
                }
                if (symbol == '"') {
                    return new AutomatonStateStepResult(factory.getInsideDoubleQuotesState(),
                            CommandToken.getEmptyToken());
                }
                inStream.rollBack();
                return new AutomatonStateStepResult(factory.getBasicArgumentParsingState(),
                        CommandToken.getEmptyToken());
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    CommandToken.getEmptyToken());
        }
    }

    /**
     * Parses command name until space character, quotation mark or pipe.
     * In case of assignment command parses variable name until '=' character.
     */
    private class BasicCommandParsingState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            boolean escaped = false;
            StringBuilder commandNameBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (escaped) {
                    commandNameBuilder.append(symbol);
                    escaped = false;
                    continue;
                }
                if (Character.isSpaceChar(symbol)) {
                    return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                            finalizeAsCommand(commandNameBuilder));
                }
                switch (symbol) {
                    case ('\''): {
                        return new AutomatonStateStepResult(factory.getInsideSingleQuotesState(),
                                finalizeAsCommand(commandNameBuilder));
                    }
                    case ('"'): {
                        return new AutomatonStateStepResult(factory.getInsideDoubleQuotesState(),
                                finalizeAsCommand(commandNameBuilder));
                    }
                    case ('|'): {
                        return new AutomatonStateStepResult(factory.getPipeState(),
                                finalizeAsCommand(commandNameBuilder));
                    }
                    case ('='): {
                        return new AutomatonStateStepResult(factory.getAssignmentCommandState(),
                                finalizeAsArgument(commandNameBuilder));
                    }
                    default: {
                        if (symbol == '\\') {
                            escaped = true;
                        }
                        commandNameBuilder.append(symbol);
                    }
                }
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    finalizeAsCommand(commandNameBuilder));
        }
    }

    /**
     * Parses single '=' character
     */
    private class AssignmentCommandState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                    new CommandToken("=", CommandTokenType.COMMAND));
        }
    }

    /**
     * Basic argument parsing.
     * Parses argument until it meets either space character, quotation mark or pipe
     */
    private class BasicArgumentParsingState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            boolean escaped = false;
            StringBuilder argBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (escaped) {
                    argBuilder.append(symbol);
                    escaped = false;
                    continue;
                }
                if (Character.isSpaceChar(symbol)) {
                    return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                            finalizeAsArgument(argBuilder));
                }
                if (symbol == '\'') {
                    return new AutomatonStateStepResult(factory.getInsideSingleQuotesState(),
                            finalizeAsArgument(argBuilder));
                }
                if (symbol == '"') {
                    return new AutomatonStateStepResult(factory.getInsideDoubleQuotesState(),
                            finalizeAsArgument(argBuilder));
                }
                if (symbol == '|') {
                    return new AutomatonStateStepResult(factory.getPipeState(),
                            finalizeAsArgument(argBuilder));
                }
                if (symbol == '\\') {
                    escaped = true;
                }
                argBuilder.append(symbol);
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    finalizeAsArgument(argBuilder));
        }
    }

    /**
     * State representing parsing inside quotes, parametrized by quotation mark character.
     * Parses argument until next matching quotation mark character.
     */
    private class InsideQuotesState extends NonTerminalState {

        private char quoteSymbol;

        public InsideQuotesState(char quoteSymbol) {
            this.quoteSymbol = quoteSymbol;
        }

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            boolean escaped = false;
            StringBuilder argBuilder = new StringBuilder();
            argBuilder.append(quoteSymbol);
            for (Character symbol: inStream) {
                if (escaped) {
                    escaped = false;
                } else if (symbol == '\\') {
                    escaped = true;
                } else if (symbol == quoteSymbol) {
                    argBuilder.append(quoteSymbol);
                    return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                            finalizeAsArgument(argBuilder));
                }
                argBuilder.append(symbol);
            }
            argBuilder.append(quoteSymbol);
            return new AutomatonStateStepResult(factory.getUnmatchedState(),
                    finalizeAsArgument(argBuilder));
        }
    }

    /**
     * Pipe state, doesn't read any characters from input, just indicates pipe
     * and passes to the SpaceConsumingStartingState
     */
    private class PipeState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return new AutomatonStateStepResult(factory.getSpaceConsumingStartingState(),
                    new CommandToken("|", CommandTokenType.PIPE));
        }
    }

    /**
     * FinalState of a successful parsing
     */
    private class FinalState extends TerminalState {
        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return null;
        }

        /**
         * Wraps result of a parsing with SuccessfulParsing class
         * @param result
         * @param <T>
         * @return
         */
        @Override
        public <T> ParsingResult<T> wrapResult(T result) {
            return new SuccessfulParsing<>(result);
        }
    }

    /**
     * FinalState for unsuccessful parsing, when unmatched quotes are present
     */
    private class UnmatchedQuotesFinalState extends TerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return null;
        }

        /**
         * Discards result, but wraps error message into FailedParsing class
         * @param result
         * @param <T>
         * @return
         */
        @Override
        public <T> ParsingResult<T> wrapResult(T result) {
            return new FailedParsing<>(CommandParsingErrorMessages.UNMATCHED_QUOTES);
        }
    }
}
