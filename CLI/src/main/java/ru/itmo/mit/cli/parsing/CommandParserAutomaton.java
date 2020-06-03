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

    private CommandToken finalizeAsCommand(StringBuilder stringBuilder) {
        return new CommandToken(stringBuilder.toString(),
                CommandTokenType.COMMAND);
    }

    private CommandToken finalizeAsArgument(StringBuilder stringBuilder) {
        return new CommandToken(stringBuilder.toString(),
                CommandTokenType.ARGUMENT);
    }

    private CommandToken finalizeAsArgumentOrEmpty(StringBuilder stringBuilder) {
        String result = stringBuilder.toString();
        if (result == "") {
            return CommandToken.getEmptyToken();
        }
        return new CommandToken(result,
                CommandTokenType.ARGUMENT);
    }

    private class ComParserAutoStateFactory {

        private final SpaceConsumingStartingState spaceConsumingStartingState;
        private final SpaceConsumingState spaceConsumingState;
        private final BasicCommandParsingState basicCommandParsingState;
        private final AssignmentCommandState assignmentCommandState;
        private final BasicArgumentParsingState basicArgumentParsingState;
        private final InsideQuotesState insideQuotesState;
        private final PipeState pipeState;
        private final FinalState finalState;

        private ComParserAutoStateFactory() {
            spaceConsumingStartingState = new SpaceConsumingStartingState();
            spaceConsumingState = new SpaceConsumingState();
            basicCommandParsingState = new BasicCommandParsingState();
            assignmentCommandState = new AssignmentCommandState();
            basicArgumentParsingState = new BasicArgumentParsingState();
            insideQuotesState = new InsideQuotesState();
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

        public AssignmentCommandState getAssignmentCommandState() {
            return assignmentCommandState;
        }

        public BasicArgumentParsingState getBasicArgumentParsingState() {
            return basicArgumentParsingState;
        }

        public InsideQuotesState getInsideQuotesState() {
            return insideQuotesState;
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
/*                if (symbol == '|') {
                    // bash warns of unexpected symbol
                }*/
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
                if (symbol == '|') {
                    return new AutomatonStateStepResult(factory.getPipeState(),
                            CommandToken.getEmptyToken());
                }
                if (symbol == '"' | symbol == '\'') {
                    return new AutomatonStateStepResult(factory.getInsideQuotesState(),
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

    private class BasicCommandParsingState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder commandNameBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (Character.isSpaceChar(symbol)) {
                    return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                            finalizeAsCommand(commandNameBuilder));
                }
                if (symbol == '\'' || symbol == '"') {
                    return new AutomatonStateStepResult(factory.getInsideQuotesState(),
                            finalizeAsCommand(commandNameBuilder));
                }
                if (symbol == '|') {
                    return new AutomatonStateStepResult(factory.getPipeState(),
                            finalizeAsCommand(commandNameBuilder));
                }
                if (symbol == '=') {
                    return new AutomatonStateStepResult(factory.getAssignmentCommandState(),
                            finalizeAsArgument(commandNameBuilder));
                }
                commandNameBuilder.append(symbol);
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    finalizeAsCommand(commandNameBuilder));
        }
    }

    private class AssignmentCommandState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                    new CommandToken("=", CommandTokenType.COMMAND));
        }
    }

    private class BasicArgumentParsingState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder argBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (Character.isSpaceChar(symbol)) {
                    return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                            finalizeAsArgument(argBuilder));
                }
                if (symbol == '\'' || symbol == '"') {
                    return new AutomatonStateStepResult(factory.getInsideQuotesState(),
                            finalizeAsArgument(argBuilder));
                }
                if (symbol == '|') {
                    return new AutomatonStateStepResult(factory.getPipeState(),
                            finalizeAsArgument(argBuilder));
                }
                argBuilder.append(symbol);
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    finalizeAsArgument(argBuilder));
        }
    }

    private class InsideQuotesState extends NonTerminalState {
        // Нужно все-таки разделить на два типа кавычек
        // в одинарных там всегда экранирование
        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder argBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                // Вроде, обещали, что вложенных кавычек не встречается
                if (symbol == '"' || symbol == '\'') {
                    return new AutomatonStateStepResult(factory.getSpaceConsumingState(),
                            finalizeAsArgument(argBuilder));
                }
                argBuilder.append(symbol);
            }
            return new AutomatonStateStepResult(factory.getFinalState(),
                    finalizeAsArgument(argBuilder));
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
