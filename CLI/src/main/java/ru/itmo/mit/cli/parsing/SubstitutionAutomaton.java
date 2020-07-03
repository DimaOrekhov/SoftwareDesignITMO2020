package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.execution.domain.Namespace;
import ru.itmo.mit.cli.parsing.domain.*;


/**
 * Substitutor implementation based on Automaton abstract class
 */
public final class SubstitutionAutomaton extends Automaton<Character, String> implements Substitutor {

    private final Namespace namespace;
    private final SubAutoStateFactory stateFactory;

    public SubstitutionAutomaton(Namespace namespace) {
        this.namespace = namespace;
        stateFactory = new SubAutoStateFactory();
    }

    /**
     * @param inputString String which possibly contains variables
     * @return ParsingResult, which, in case no error has occurred, contains
     * String with values substituted for variable names
     */
    @Override
    public ParsingResult<String> substitute(String inputString) {
        StringBuilder stringBuilder = new StringBuilder();
        TerminalState terminalState = process(new StringPosStream(inputString),
                new StringBuilderAsHandler(stringBuilder));
        return terminalState.wrapResult(stringBuilder.toString());
    }

    @Override
    protected AutomatonState getStartingState() {
        return new BasicState();
    }

    /**
     * Factory class, provides states of the automaton, each possible state is a singleton
     */
    private class SubAutoStateFactory {
        private final BasicState basicState;
        private final InsideSingleQuotes insideSingleQuotes;
        private final InsideDoubleQuotes insideDoubleQuotes;
        private final ReadingVariableName readingVariableNameFromBasic;
        private final ReadingVariableName readingVariableNameFromQuotes;
        private final FinalState finalState;

        private SubAutoStateFactory() {
            basicState = new BasicState();
            insideSingleQuotes = new InsideSingleQuotes();
            insideDoubleQuotes = new InsideDoubleQuotes();
            readingVariableNameFromBasic = new ReadingVariableName(basicState);
            readingVariableNameFromQuotes = new ReadingVariableName(insideDoubleQuotes);
            finalState = new FinalState();
        }

        public BasicState getBasicState() {
            return basicState;
        }

        public InsideDoubleQuotes getInsideDoubleQuotes() {
            return insideDoubleQuotes;
        }

        public ReadingVariableName getReadingVariableNameFromBasic() {
            return readingVariableNameFromBasic;
        }

        public ReadingVariableName getReadingVariableNameFromQuotes() {
            return readingVariableNameFromQuotes;
        }

        public InsideSingleQuotes getInsideSingleQuotes() {
            return insideSingleQuotes;
        }

        public FinalState getFinalState() {
            return finalState;
        }
    }

    /**
     * Basic automaton state
     * Reads Character and appends it to a resulting sequence
     * In case '$' appears passes to ReadingVariableName state
     * In case quotation mark character appears passes to corresponding InsideQuotesState
     */
    private class BasicState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            boolean escaped = false;
            StringBuilder stringBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (escaped) {
                    stringBuilder.append(symbol);
                    escaped = false;
                    continue;
                }
                switch (symbol) {
                    case ('$'): {
                        return new AutomatonStateStepResult(stateFactory.getReadingVariableNameFromBasic(),
                                stringBuilder.toString());
                    }
                    case ('"'): {
                        stringBuilder.append(symbol);
                        return new AutomatonStateStepResult(stateFactory.getInsideDoubleQuotes(),
                                stringBuilder.toString());
                    }
                    case ('\''): {
                        stringBuilder.append(symbol);
                        return new AutomatonStateStepResult(stateFactory.getInsideSingleQuotes(),
                                stringBuilder.toString());
                    }
                    default: {
                        if (symbol == '\\') {
                            escaped = true;
                        }
                        stringBuilder.append(symbol);
                        break;
                    }
                }
            }
            return new AutomatonStateStepResult(stateFactory.getFinalState(),
                    stringBuilder.toString());
        }
    }

    /**
     * Just like BasicState, but treats single quotation marks simply as a character
     * Returns to BasicState when '"' occurs
     */
    private class InsideDoubleQuotes extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            boolean escaped = false;
            StringBuilder stringBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (escaped) {
                    stringBuilder.append(symbol);
                    escaped = false;
                    continue;
                }
                switch (symbol) {
                    case ('$'): {
                        return new AutomatonStateStepResult(stateFactory.getReadingVariableNameFromQuotes(),
                                stringBuilder.toString());
                    }
                    case ('"'): {
                        stringBuilder.append(symbol);
                        return new AutomatonStateStepResult(stateFactory.getBasicState(),
                                stringBuilder.toString());
                    }
                    default: {
                        if (symbol == '\\') {
                            escaped = true;
                        }
                        stringBuilder.append(symbol);
                        break;
                    }
                }
            }
            return new AutomatonStateStepResult(stateFactory.getFinalState(),
                    stringBuilder.toString());
        }

    }

    /**
     * Reads variable name until meets neither letter nor digit
     * Then passes control to parentState (BasicState or InsideDoubleQuotes state)
     * and substitutes read variable name with its associated value.
     * In case no value is associated with a given name, empty string is substituted for.
     */
    private class ReadingVariableName extends NonTerminalState {

        private AutomatonState parentState;

        public ReadingVariableName(AutomatonState parentState) {
            this.parentState = parentState;
        }

        @Override
        public AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (Character.isLetter(symbol) || Character.isDigit(symbol)) {
                    stringBuilder.append(symbol);
                    continue;
                }
                inStream.rollBack();
                return new AutomatonStateStepResult(parentState, finalize(stringBuilder));
            }
            return new AutomatonStateStepResult(stateFactory.getFinalState(),
                    finalize(stringBuilder));
        }

        private String finalize(StringBuilder stringBuilder) {
            String variableName = stringBuilder.toString();
            String variableValue = namespace.get(variableName);
            return variableValue == null ? "" : variableValue;
        }
    }


    /**
     * Just appends every character from the stream to resulting string
     * until single quotation mark is met
     */
    private class InsideSingleQuotes extends NonTerminalState {

        @Override
        public AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                stringBuilder.append(symbol);
                if (symbol == '\'') {
                    return new AutomatonStateStepResult(stateFactory.getBasicState(),
                            stringBuilder.toString());
                }
            }
            return new AutomatonStateStepResult(stateFactory.getFinalState(),
                    stringBuilder.toString());
        }
    }

    /**
     * FinalState of successful parsing
     */
    private class FinalState extends TerminalState {
        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return null;
        }

        @Override
        public <T> ParsingResult<T> wrapResult(T result) {
            return new SuccessfulParsing<>(result);
        }
    }
}
