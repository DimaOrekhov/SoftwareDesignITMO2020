package ru.itmo.mit.cli.parsing.domain;


public abstract class Automaton<input, output> {

    /**
     * This method describes basic automaton traversal
     * Proceed from starting state to the terminal
     * Performing one step at a time
     * @param inStream
     * @param outStream
     * @return
     */
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


    /**
     * Inner class representing result of a step
     * Effectively it is a pair that stores next State
     * and some result obtained at a current step
     */
    protected class AutomatonStateStepResult {

        private final AutomatonState nextState;
        private final output result;

        public AutomatonStateStepResult(AutomatonState nextState, output result) {
            this.nextState = nextState;
            this.result = result;
        }
    }

    // Choose abstract class over interface so
    // I could capture same generic instance of the outer class
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
