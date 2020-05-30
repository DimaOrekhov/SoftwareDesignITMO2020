package ru.itmo.mit.cli.parsing;

import ru.itmo.mit.cli.domain.Namespace;
import ru.itmo.mit.cli.parsing.domain.Automaton;
import ru.itmo.mit.cli.parsing.domain.AutomatonInputStream;
import ru.itmo.mit.cli.parsing.domain.Substitutor;

import java.util.Map;

public final class SubstitutionAutomaton extends Automaton<Character, String> implements Substitutor {

    private final Namespace namespace;
    private final SubAutoStateFactory stateFactory;

    public SubstitutionAutomaton(Namespace namespace) {
        this.namespace = namespace;
        stateFactory = new SubAutoStateFactory();
    }

    @Override
    public String substitute(String inputString) {
        StringBuilder stringBuilder = new StringBuilder();
        process(new StringPosStream(inputString),
                new StringBuilderAsHandler(stringBuilder));
        return stringBuilder.toString();
    }

    @Override
    protected AutomatonState getStartingState() {
        return new BasicState();
    }

    // Делаю синглтоны сотояний
    private class SubAutoStateFactory {
        private final BasicState basicState;
        private final ReadingVariableName readingVariableName;
        private final InsideSingleQuotes insideSingleQuotes;
        private final FinalState finalState;

        private SubAutoStateFactory() {
            basicState = new BasicState();
            readingVariableName = new ReadingVariableName();
            insideSingleQuotes = new InsideSingleQuotes();
            finalState = new FinalState();
        }

        public BasicState getBasicState() {
            return basicState;
        }

        public ReadingVariableName getReadingVariableName() {
            return readingVariableName;
        }

        public InsideSingleQuotes getInsideSingleQuotes() {
            return insideSingleQuotes;
        }

        public FinalState getFinalState() {
            return finalState;
        }
    }

    private class BasicState extends NonTerminalState {

        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                switch (symbol) {
                    case ('$'): {
                        return new AutomatonStateStepResult(stateFactory.getReadingVariableName(),
                                stringBuilder.toString());
                    }
                    case ('\''): {
                        stringBuilder.append(symbol);
                        return new AutomatonStateStepResult(stateFactory.getInsideSingleQuotes(),
                                stringBuilder.toString());
                    }
                    default: {
                        stringBuilder.append(symbol);
                        break;
                    }
                }
            }
            return new AutomatonStateStepResult(stateFactory.getFinalState(),
                    stringBuilder.toString());
        }
    }

    private class ReadingVariableName extends NonTerminalState {

        @Override
        public AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Character symbol: inStream) {
                if (Character.isLetter(symbol) || Character.isDigit(symbol)) {
                    stringBuilder.append(symbol);
                    continue;
                }
                inStream.rollBack();
                return new AutomatonStateStepResult(stateFactory.getBasicState(), finalize(stringBuilder));
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

    private class FinalState extends TerminalState {
        @Override
        protected AutomatonStateStepResult stateStep(AutomatonInputStream<Character> inStream) {
            return null;
        }
    }
}
