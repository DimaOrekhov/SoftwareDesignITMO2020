package ru.itmo.mit.cli.parsing.domain;

public abstract class Automaton<input, output> {

    public TerminalState process(AutomatonInputStream<input> inStream,
                        AutomatonOutputHandler<output> outStream) {
        AutomatonState currState = getStartingState();
        while (!currState.isTerminal()) {
            AutomatonStateStepResult stepResult = currState.stateStep(inStream);
            outStream.handle(stepResult.result);
            currState = stepResult.nextState;
        }
        outStream.finalizeHandler();
        return (TerminalState)currState;
    }

    protected abstract AutomatonState getStartingState();


    protected class AutomatonStateStepResult {

        private final AutomatonState nextState;
        private final output result;

        public AutomatonStateStepResult(AutomatonState nextState, output result) {
            this.nextState = nextState;
            this.result = result;
        }
    }

    // Выбрал абстрактный класс, а не интерфейс, чтобы можно было
    // зацепиться за дженерики outer класса.
    protected abstract class AutomatonState {

        protected abstract AutomatonStateStepResult stateStep(AutomatonInputStream<input> inStream);

        public abstract boolean isTerminal();

    }

    protected abstract class NonTerminalState extends AutomatonState {
        public boolean isTerminal() {
            return false;
        }
    }

    protected abstract class TerminalState extends AutomatonState {
        public abstract <T> ParsingResult<T> wrapResult(T result);
        public boolean isTerminal() {
            return true;
        }
    }

}
