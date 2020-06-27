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

    /**
     * @return Starting stating of a given automaton
     */
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

    /**
     * Automaton state abstract class
     */
    protected abstract class AutomatonState {

        /**
         * Method representing processing done in a given state
         * @param inStream iterable stream of input tokens, they controll
         *                 the processing
         * @return a pair of some processing result and a next state
         */
        protected abstract AutomatonStateStepResult stateStep(AutomatonInputStream<input> inStream);

        /**
         * @return boolean indicating whether a given object represents terminal state
         */
        public abstract boolean isTerminal();

    }

    /**
     * Abstract class for implementing non terminal automaton states
     */
    protected abstract class NonTerminalState extends AutomatonState {
        public boolean isTerminal() {
            return false;
        }
    }

    /**
     * Abstract class for implementing terminal automaton states
     */
    protected abstract class TerminalState extends AutomatonState {
        public abstract <T> ParsingResult<T> wrapResult(T result);
        public boolean isTerminal() {
            return true;
        }
    }

}
